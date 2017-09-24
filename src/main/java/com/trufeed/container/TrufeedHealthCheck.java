package com.trufeed.container;

import com.codahale.metrics.health.HealthCheck;

public class TrufeedHealthCheck extends HealthCheck {

  @Override
  protected Result check() throws Exception {
    return Result.healthy();
  }
}
