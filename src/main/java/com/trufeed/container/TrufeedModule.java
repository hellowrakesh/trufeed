package com.trufeed.container;

import com.google.inject.AbstractModule;
import com.linkedin.parseq.Engine;
import com.linkedin.parseq.EngineBuilder;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;

public class TrufeedModule extends AbstractModule {

  private final TrufeedConfiguration configuration;

  public TrufeedModule(TrufeedConfiguration configuration) {
    this.configuration = configuration;
  }

  @Override
  protected void configure() {
    bind(TrufeedConfiguration.class).toInstance(configuration);
    bind(TrufeedConfiguration.Storage.class).toInstance(configuration.getStorage());
    //    bind(TrufeedConfiguration.StoreConfig.class)
    //        .annotatedWith(Names.named("userStoreConfig"))
    //        .toInstance(configuration.getStorage().getUserStore());
    //    bind(TrufeedConfiguration.StoreConfig.class)
    //        .annotatedWith(Names.named("feedStoreConfig"))
    //        .toInstance(configuration.getStorage().getFeedStore());
    bind(Engine.class).toInstance(getEngine());
  }

  private Engine getEngine() {
    final int numCores = Runtime.getRuntime().availableProcessors();
    final ExecutorService taskScheduler = Executors.newFixedThreadPool(numCores + 1);
    final ScheduledExecutorService timerScheduler = Executors.newSingleThreadScheduledExecutor();
    return new EngineBuilder()
        .setTaskExecutor(taskScheduler)
        .setTimerScheduler(timerScheduler)
        .build();
  }
}
