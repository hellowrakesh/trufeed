package com.trufeed.api;

import com.linkedin.parseq.Engine;
import com.linkedin.parseq.Task;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.Response.Status;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public abstract class Api {

  private static final Logger LOG = LoggerFactory.getLogger(Api.class);
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
      LOG.error(
          "Internal error while handling request: " + exception.getLocalizedMessage(), exception);
      return Response.status(Status.INTERNAL_SERVER_ERROR).entity(exception.getMessage()).build();
    } catch (Exception exception) {
      LOG.error(
          "Error while processing the request: " + exception.getLocalizedMessage(), exception);
      return Response.status(Status.BAD_REQUEST).entity(exception.getMessage()).build();
    }
  }
}
