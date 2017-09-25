package com.trufeed.api;

import com.google.inject.Inject;
import com.google.inject.Singleton;
import com.linkedin.parseq.Engine;
import com.trufeed.entities.User;
import com.trufeed.service.UserService;
import io.dropwizard.jersey.PATCH;
import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

@Path("/user")
@Produces(MediaType.APPLICATION_JSON)
@Singleton
public class UserApi extends Api {

  private final UserService service;

  @Inject
  public UserApi(Engine engine, UserService service) {
    super(engine);
    this.service = service;
  }

  @GET
  @Path("/{userUuid}")
  public Response get(@PathParam("userUuid") String uuid) {
    return execute(service.get(uuid));
  }

  @Consumes(MediaType.APPLICATION_JSON)
  @POST
  public Response create(User user) {
    return execute(service.add(user));
  }

  @GET
  @Path("/{userUuid}/feeds")
  public Response getSubscribedFeeds(@PathParam("userUuid") String uuid) {
    return execute(service.getSubscribedFeeds(uuid));
  }

  @GET
  @Path("/{userUuid}/feeds/articles")
  public Response getSubscribedFeedsArticles(@PathParam("userUuid") String uuid) {
    return execute(service.getSubscribedFeedsArticles(uuid));
  }

  @PATCH
  @Path("{userUuid}/feed/{feedUuid}/subscribe")
  public Response subscribe(
      @PathParam("feedUuid") String feedUuid, @PathParam("userUuid") String userUuid) {
    return execute(service.subscribe(userUuid, feedUuid));
  }

  @PATCH
  @Path("{userUuid}/feed/{feedUuid}/unsubscribe")
  public Response unsubscribe(
      @PathParam("feedUuid") String feedUuid, @PathParam("userUuid") String userUuid) {
    return execute(service.unsubscribe(userUuid, feedUuid));
  }
}
