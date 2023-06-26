package com.msb.ibs.edge.service.configs;

import com.msb.ibs.edge.service.filters.TokenConverterFilter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.cloud.context.config.annotation.RefreshScope;
import org.springframework.cloud.gateway.filter.ratelimit.KeyResolver;
import org.springframework.cloud.gateway.filter.ratelimit.RedisRateLimiter;
import org.springframework.cloud.gateway.route.RouteLocator;
import org.springframework.cloud.gateway.route.builder.RouteLocatorBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.http.HttpStatus;
import reactor.core.publisher.Mono;

import java.io.IOException;
import java.util.concurrent.TimeoutException;

@Configuration
@RefreshScope
public class RouteConfig {
    @Value("${ibs.backend.baseUrl}")
    private String baseUrl;

    @Autowired
    TokenConverterFilter tokenConverterFilter;

    @Bean
    @Primary
    public RedisRateLimiter myRateLimiter() {
        return new RedisRateLimiter(2, 2, 1);
    }

    @Bean
    @Primary
    KeyResolver userKeyResolver() {
        return exchange -> Mono.just("Test");
        //return exchange -> Mono.just(String.valueOf(exchange.getRequest().getHeaders().get("Authorization")));
    }

    @Bean
    public RouteLocator customRouteLocator(RouteLocatorBuilder builder) {
        return builder.routes()
                .route("login", route -> route
                        .path("/api/v1/corp-customer/**")
                        .filters(filter -> filter
                                .rewritePath("/api/v1/corp-customer/(?<segment>.*)", "/ibs-corp-customer/$\\{segment}")
                                .requestRateLimiter(rateLimit -> rateLimit
                                        .setRateLimiter(myRateLimiter())
                                        .setKeyResolver(userKeyResolver())
                                        .setStatusCode(HttpStatus.TOO_MANY_REQUESTS))
                                .circuitBreaker(config -> config
                                        .setName("myCircuitBreaker")
                                        .setFallbackUri("forward:/fallback"))
                                .retry(retry -> retry
                                        .setRetries(3)
                                        .setStatuses(HttpStatus.INTERNAL_SERVER_ERROR)
                                        .setExceptions(IOException.class, TimeoutException.class)
                                )
                                .filter(tokenConverterFilter)
                        )
                        .uri(baseUrl))
                .build();
    }
}
