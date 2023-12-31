package com.msb.ibs.edge.service.rest;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Mono;

@RestController
public class FallbackController {
    @RequestMapping("/fallback")
    public Mono<String> fallback() {
        return Mono.just("Fallback response");
    }
}
