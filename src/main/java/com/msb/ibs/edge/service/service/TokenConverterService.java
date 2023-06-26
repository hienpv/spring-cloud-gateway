package com.msb.ibs.edge.service.service;

import org.springframework.cache.annotation.Cacheable;

public interface TokenConverterService {
    @Cacheable(cacheNames = "token-converter", key = "#clientToken", unless="#result == null")
    String getServiceToken(String clientToken);
}
