package com.trufeed.repository;

import static com.trufeed.utils.CommonUtils.fromJsonStringToObject;
import static com.trufeed.utils.CommonUtils.fromObjectToJsonString;

import com.google.common.collect.Lists;
import com.google.inject.Inject;
import com.linkedin.parseq.Task;
import com.trufeed.container.TrufeedConfiguration.Storage;
import com.trufeed.container.TrufeedConfiguration.Store;
import com.trufeed.entities.FileSerializable;
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
import java.util.stream.Stream;
import org.apache.commons.lang3.StringUtils;
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

  public Task<Boolean> exists(String... paths) {
    return Task.callable(
        "check if path exists",
        () -> {
          return Files.exists(resolve(paths));
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
        .flatMap(
            value -> {
              if (StringUtils.isEmpty(value)) {
                throw new RuntimeException("Entity not found for clazz: " + clazz);
              }
              return Task.value(fromJsonStringToObject(value, clazz));
            });
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

  public Task<Stream<String>> getFileContentsAsStream(String... paths) {
    try {
      return Task.value(Files.lines(resolve(paths)));
    } catch (Exception exception) {
      LOG.error("Error while reading files as stream", exception);
      throw new RuntimeException("Error while reading files as stream", exception);
    }
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
            // aquire shared read lock while reading from the file
            fileChannel.lock(0, Long.MAX_VALUE, true);
            StringBuilder builder = new StringBuilder();
            buffer.flip();
            while (noOfBytesRead != -1) {
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

  public Task<Boolean> saveFileContents(FileSerializable object, String... paths) {
    return saveFileContents(Lists.newArrayList(object), paths);
  }

  public Task<Boolean> saveFileContents(List<FileSerializable> objects, String... paths) {
    return Task.callable(
        "write contents to a file",
        () -> {
          FileChannel fileChannel = null;
          try {
            fileChannel = FileChannel.open(resolve(paths), StandardOpenOption.APPEND);
            // aquire an exclusive write lock while writing to the file
            fileChannel.lock();

            ByteBuffer buffer = ByteBuffer.allocate(1024);

            for (FileSerializable object : objects) {
              String line = fromObjectToJsonString(object) + "\n";
              buffer.put(line.getBytes());
              buffer.flip();
              while (buffer.hasRemaining()) {
                fileChannel.write(buffer);
              }
              buffer.clear();
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
