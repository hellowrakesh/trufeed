package com.trufeed.repository;

import static com.trufeed.utils.JsonUtils.fromJsonString;

import java.io.Serializable;
import java.nio.ByteBuffer;
import java.nio.channels.FileChannel;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.linkedin.parseq.Task;
import com.trufeed.container.TrufeedConfiguration.Storage;
import com.trufeed.container.TrufeedConfiguration.Store;

public class FileRepository {

  protected static Logger LOG = LoggerFactory.getLogger(FileRepository.class);
  protected Path rootPath = null;

  public FileRepository(Storage storage, Store store) {
    String rootDir = storage.getRootDir() + "/" + store.getPath();
    this.rootPath = Paths.get(rootDir);

    // check if the root_dir doesn't exist, create it
    Task.value(
        exists("/")
            .flatMap(exist -> createDirectory("/"))
            .onFailure(
                "error while creating directory: " + rootDir,
                throwable -> {
                  throw new RuntimeException("Error while creating directory: " + rootDir);
                }));

    boolean isSuccess = exists("/").flatMap(exist -> createDirectory("/")).get();
    if (!isSuccess) {
      throw new RuntimeException("Error while creating root_dir: " + rootDir);
    }
    LOG.debug("Successfully created root_dir: " + rootDir);
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
    return getFileContents(paths).flatMap(value -> Task.value(fromJsonString(value, clazz)));
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
              while (buffer.hasRemaining()) {
                builder.append(new String(buffer.array()));
              }
              buffer.clear();
              noOfBytesRead = fileChannel.read(buffer);
            }
            return builder.toString();
          } catch (Exception exception) {
            LOG.error("Error while writing to the  file: " + paths, exception);
            return null;
          } finally {
            if (fileChannel != null) {
              fileChannel.close();
            }
          }
        });
  }

  public Task<Boolean> saveFileContents(Serializable object, String... paths) {
    return Task.callable(
        "write contents to a file",
        () -> {
          FileChannel fileChannel = null;
          try {
            fileChannel = FileChannel.open(resolve(paths), StandardOpenOption.APPEND);

            ByteBuffer buffer = ByteBuffer.allocate(1024);

            buffer.put(object.toString().getBytes());
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
            Files.createFile(resolve(paths));
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
