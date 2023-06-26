package com.msb.ibs.edge.service.filters;

import com.msb.ibs.edge.service.service.TokenConverterService;
import io.netty.util.internal.StringUtil;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cloud.gateway.filter.GatewayFilter;
import org.springframework.cloud.gateway.filter.GatewayFilterChain;
import org.springframework.context.annotation.Lazy;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;

import java.util.Objects;

@Component
@Slf4j
@RequiredArgsConstructor(onConstructor = @__(@Lazy))
public class TokenConverterFilter implements GatewayFilter {
    private final TokenConverterService tokenConverterService;

    @Override
    public Mono<Void> filter(ServerWebExchange exchange, GatewayFilterChain chain) {
        ServerHttpRequest request = exchange.getRequest().mutate()
                .headers(httpHeaders -> {
                    String clientToken = String.valueOf(Objects.requireNonNull(httpHeaders.get("Authorization")).get(0));
                    String serviceToken = tokenConverterService.getServiceToken(clientToken);
                    if (!StringUtil.isNullOrEmpty(serviceToken)) {
                        httpHeaders.remove("Authorization");
                        httpHeaders.add("Authorization", String.format("Bearer %s", serviceToken));
                    }
                }).build();

        return chain.filter(exchange.mutate()
                .request(request)
                .build());
    }


}
