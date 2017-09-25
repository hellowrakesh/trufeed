package com.trufeed.container;

import com.google.inject.Guice;
import com.google.inject.Injector;
import com.trufeed.api.FeedApi;
import com.trufeed.api.UserApi;
import io.dropwizard.Application;
import io.dropwizard.configuration.EnvironmentVariableSubstitutor;
import io.dropwizard.configuration.SubstitutingSourceProvider;
import io.dropwizard.setup.Bootstrap;
import io.dropwizard.setup.Environment;

public class TrufeedServer extends Application<TrufeedConfiguration> {

  public static void main(String[] args) throws Exception {
    new TrufeedServer().run(args);
  }

  @Override
  public String getName() {
    return "trufeed";
  }

  @Override
  public void initialize(Bootstrap<TrufeedConfiguration> bootstrap) {
    // Enable variable substitution with environment variables
    bootstrap.setConfigurationSourceProvider(
        new SubstitutingSourceProvider(
            bootstrap.getConfigurationSourceProvider(), new EnvironmentVariableSubstitutor(true)));
  }

  @Override
  public void run(TrufeedConfiguration configuration, Environment environment) throws Exception {
    Injector injector = Guice.createInjector(new TrufeedModule(configuration));
    environment.healthChecks().register("trufeed", new TrufeedHealthCheck());
    environment.jersey().register(injector.getInstance(UserApi.class));
    environment.jersey().register(injector.getInstance(FeedApi.class));
  }
}
