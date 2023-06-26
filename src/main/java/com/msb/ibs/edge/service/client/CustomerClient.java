package com.msb.ibs.edge.service.client;

import com.msb.ibs.edge.service.model.request.TokenConverterRequest;
import com.msb.ibs.edge.service.model.response.TokenConverterResponse;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

import javax.validation.Valid;

@FeignClient(value = "${feign.customer.name}", url = "${feign.customer.baseUrl}")
public interface CustomerClient {
    @PostMapping(value = "${feign.customer.tokenConverter}")
    ResponseEntity<TokenConverterResponse> tokenConverter(@RequestBody @Valid TokenConverterRequest tokenConverterRequest);
}
