package com.trufeed.repository;

import static com.trufeed.utils.CommonUtils.fromJsonStringToObject;
import static com.trufeed.utils.CommonUtils.fromObjectToJsonString;

import com.google.inject.Inject;
import com.linkedin.parseq.Task;
import com.trufeed.container.TrufeedConfiguration.Storage;
import com.trufeed.container.TrufeedConfiguration.Store;
import com.trufeed.entities.LogSerializable;
import java.io.File;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.channels.FileChannel;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;
import java.util.List;
import java.util.stream.Collectors;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class FileRepository {

  protected static Logger LOG = LoggerFactory.getLogger(FileRepository.class);
  protected Path rootPath = null;

  public FileRepository(Storage storage, Store store) {
    this.rootPath = Paths.get(storage.getRootDir(), store.getPath());
  }

  @Inject
  public void init() throws Exception {
    // create root dir
    Files.createDirectories(rootPath);
    LOG.info("Successfully created root_dir: " + rootPath.toString());
  }

  public Task<Boolean> exists(String path) {
    return Task.callable(
        "check if path exists",
        () -> {
          return Files.exists(rootPath.resolve(path));
        });
  }

  public Task<Boolean> createDirectory(String... paths) {
    return Task.callable(
        "check if path exists",
        () -> {
          try {
            Files.createDirectory(resolve(paths));
          } catch (Exception exception) {
            LOG.error("Error while creating dir: " + paths, exception);
            return false;
          }
          return true;
        });
  }

  public <T> Task<T> getEntity(Class<T> clazz, String... paths) {
    return getFileContents(paths)
        .flatMap(value -> Task.value(fromJsonStringToObject(value, clazz)));
  }

  public Task<List<String>> getAllFileNames(String... paths) {
    return getAllFiles(paths)
        .flatMap(
            files ->
                Task.value(
                    files
                        .stream()
                        .filter(x -> x.isFile())
                        .map(file -> file.getName())
                        .collect(Collectors.toList())));
  }

  public Task<List<File>> getAllFiles(String... paths) {
    Path path = resolve(paths);
    return Task.callable(
        () -> {
          try {
            return Files.list(path)
                .map(x -> x.toFile())
                .filter(x -> x.isFile())
                .collect(Collectors.toList());
          } catch (IOException exception) {
            LOG.error("Error while getting list of files from path: " + path, exception);
            throw new RuntimeException(
                "Error while getting list of files from path: " + path, exception);
          }
        });
  }

  public Task<String> getFileContents(String... paths) {
    return Task.callable(
        "read contents from a file",
        () -> {
          FileChannel fileChannel = null;
          try {
            fileChannel = FileChannel.open(resolve(paths), StandardOpenOption.READ);

            ByteBuffer buffer = ByteBuffer.allocate(1024);

            int noOfBytesRead = fileChannel.read(buffer);
            // aquire an exclusive write lock while writing to the file
            fileChannel.lock(0, Long.MAX_VALUE, true);
            StringBuilder builder = new StringBuilder();
            while (noOfBytesRead != -1) {
              buffer.flip();
              builder.append(new String(buffer.array()));
              buffer.clear();
              noOfBytesRead = fileChannel.read(buffer);
            }
            return builder.toString();
          } catch (Exception exception) {
            LOG.error("Error while reading the file: " + paths, exception);
            return null;
          } finally {
            if (fileChannel != null) {
              fileChannel.close();
            }
          }
        });
  }

  public Task<Boolean> saveFileContents(LogSerializable object, String... paths) {
    return Task.callable(
        "write contents to a file",
        () -> {
          FileChannel fileChannel = null;
          try {
            fileChannel = FileChannel.open(resolve(paths), StandardOpenOption.APPEND);

            ByteBuffer buffer = ByteBuffer.allocate(1024);

            buffer.put(fromObjectToJsonString(object).getBytes());
            buffer.flip();

            // aquire an exclusive write lock while writing to the file
            fileChannel.lock();
            while (buffer.hasRemaining()) {
              fileChannel.write(buffer);
            }
          } catch (Exception exception) {
            LOG.error("Error while writing to the  file: " + paths, exception);
            return false;
          } finally {
            if (fileChannel != null) {
              fileChannel.close();
            }
          }
          return true;
        });
  }

  public Task<Boolean> createEmptyFile(String... paths) {
    return Task.callable(
        "check if path exists",
        () -> {
          try {
            Path path = resolve(paths);
            Files.createDirectories(path.getParent());
            Files.createFile(path);
          } catch (Exception exception) {
            LOG.error("Error while creating file: " + paths, exception);
            return false;
          }
          return true;
        });
  }

  public Task<Boolean> deleteFile(String... paths) {
    return Task.callable(
        "check if path exists",
        () -> {
          try {
            Files.delete(resolve(paths));
          } catch (Exception exception) {
            LOG.error("Error while deleting file: " + paths, exception);
            return false;
          }
          return true;
        });
  }

  protected Path resolve(String... paths) {
    Path p = rootPath;
    for (String path : paths) {
      p = p.resolve(path);
    }
    return p;
  }
}
