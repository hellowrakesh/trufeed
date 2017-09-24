package com.trufeed.api;

import com.linkedin.parseq.Engine;
import com.linkedin.parseq.Task;

public abstract class Api {

  protected final Engine engine;

  public Api(Engine engine) {
    this.engine = engine;
  }

  protected <T> T execute(Task<T> task) {
    try {
      engine.run(task);
      task.await();
      return task.get();
    } catch (InterruptedException exception) {
      throw new RuntimeException(exception);
    }
  }
}
