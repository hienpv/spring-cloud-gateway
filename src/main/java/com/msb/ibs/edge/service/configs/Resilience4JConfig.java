package com.msb.ibs.edge.service.configs;

import io.github.resilience4j.circuitbreaker.CircuitBreakerConfig;
import io.github.resilience4j.timelimiter.TimeLimiterConfig;
import org.springframework.cloud.circuitbreaker.resilience4j.ReactiveResilience4JCircuitBreakerFactory;
import org.springframework.cloud.circuitbreaker.resilience4j.Resilience4JConfigBuilder;
import org.springframework.cloud.client.circuitbreaker.Customizer;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.time.Duration;

@Configuration
public class Resilience4JConfig {
	/**
	 * The very simple configuration contains default circuit breaker settings and defines timeout duration using TimeLimiterConfig.
	 */
	@Bean
	public Customizer<ReactiveResilience4JCircuitBreakerFactory> defaultCustomizer() {
		return factory -> factory.configureDefault(id -> new Resilience4JConfigBuilder(id)
				.circuitBreakerConfig(CircuitBreakerConfig.custom()
						.slidingWindowSize(5) //so luong request su dung de tinh toan ty le loi
						.minimumNumberOfCalls(5)
						.permittedNumberOfCallsInHalfOpenState(5)
						// number of call in HALF OPEN STATE
						// success >= 5 -> CLOSE STATE
						// it nhat 1 lan fail -> OPEN STATE
						.failureRateThreshold(60.0F)//Fail >= 50% -> OPEN status
						.automaticTransitionFromOpenToHalfOpenEnabled(true) //Flag auto OPEN->HALF OPEN
						.waitDurationInOpenState(Duration.ofMillis(30)) // Open state > 30ml -> HALF OPEN STATE
                        //.slowCallDurationThreshold(Duration.ofMillis(200))//Time response >= 200 millisecond -> slow call
                        //.slowCallRateThreshold(50.0F) //slow call >= 50% -> OPEN status
						//.recordExceptions(IOException.class, TimeoutException.class)
						//.ignoreExceptions(BusinessException.class, OtherBusinessException.class)
						.build())
				.timeLimiterConfig(TimeLimiterConfig.custom().timeoutDuration(Duration.ofSeconds(60)).build())
				.build());
	}

}
