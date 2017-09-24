package com.trufeed.api;

import com.google.inject.Singleton;
import com.trufeed.entities.Feed;
import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;

@Path("/feed")
@Produces(MediaType.APPLICATION_JSON)
@Singleton
public class FeedApi {

  @GET
  @Path("/{feedUuid}")
  public Feed get(@PathParam("feedUuid") String uuid) {
    return null;
  }

  @Consumes(MediaType.APPLICATION_JSON)
  @POST
  public Feed create(Feed feed) {
    return null;
  }
}
