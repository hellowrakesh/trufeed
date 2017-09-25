package com.trufeed.api;

import com.google.inject.Inject;
import com.google.inject.Singleton;
import com.linkedin.parseq.Engine;
import com.trufeed.entities.Feed;
import com.trufeed.service.FeedService;
import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

@Path("/feed")
@Produces(MediaType.APPLICATION_JSON)
@Singleton
public class FeedApi extends Api {

  private final FeedService service;

  @Inject
  public FeedApi(Engine engine, FeedService service) {
    super(engine);
    this.service = service;
  }

  @GET
  @Path("/{feedUuid}")
  public Response get(@PathParam("feedUuid") String uuid) {
    return execute(service.get(uuid));
  }

  @Consumes(MediaType.APPLICATION_JSON)
  @POST
  public Response create(Feed feed) {
    return execute(service.add(feed));
  }
}
