package com.trufeed.api;

import com.google.inject.Inject;
import com.google.inject.Singleton;
import com.linkedin.parseq.Engine;
import com.trufeed.entities.Article;
import com.trufeed.entities.Feed;
import com.trufeed.service.FeedService;
import java.util.List;
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
  public Response getAll() {
    return execute(service.getAll());
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

  @Consumes(MediaType.APPLICATION_JSON)
  @GET
  @Path("/{feedUuid}/articles")
  public Response getArticles(@PathParam("feedUuid") String feedUuid) {
    return execute(service.getArticles(feedUuid));
  }

  @Consumes(MediaType.APPLICATION_JSON)
  @POST
  @Path("/{feedUuid}/article")
  public Response publishArticle(@PathParam("feedUuid") String feedUuid, Article article) {
    return execute(service.publishArticle(feedUuid, article));
  }

  @Consumes(MediaType.APPLICATION_JSON)
  @POST
  @Path("/{feedUuid}/articles")
  public Response publishArticles(@PathParam("feedUuid") String feedUuid, List<Article> articles) {
    return execute(service.publishArticles(feedUuid, articles));
  }
}
