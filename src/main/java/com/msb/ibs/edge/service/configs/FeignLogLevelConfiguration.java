package com.msb.ibs.edge.service.configs;

import feign.Logger.Level;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class FeignLogLevelConfiguration {

  @Bean
  public FeignRequestResponseLogging feignRequestResponseLogging() {
    return new FeignRequestResponseLogging();
  }

  @Bean
  Level feignLoggerLevel() {
    return Level.BASIC;
  }

}
