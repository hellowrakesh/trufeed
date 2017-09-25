package com.trufeed.api;

import com.linkedin.parseq.Engine;
import com.linkedin.parseq.Task;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.Response.Status;

public abstract class Api {

  protected final Engine engine;

  public Api(Engine engine) {
    this.engine = engine;
  }

  protected <T> Response execute(Task<T> task) {
    try {
      engine.run(task);
      task.await();
      return Response.ok().entity(task.get()).build();
    } catch (InterruptedException exception) {
      return Response.status(Status.INTERNAL_SERVER_ERROR).entity(exception.getMessage()).build();
    } catch (Exception exception) {
      return Response.status(Status.BAD_REQUEST).entity(exception.getMessage()).build();
    }
  }
}
