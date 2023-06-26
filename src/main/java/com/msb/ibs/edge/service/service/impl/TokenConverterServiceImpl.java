package com.msb.ibs.edge.service.service.impl;

import com.msb.ibs.edge.service.client.CustomerClient;
import com.msb.ibs.edge.service.model.request.TokenConverterRequest;
import com.msb.ibs.edge.service.model.response.TokenConverterResponse;
import com.msb.ibs.edge.service.service.TokenConverterService;
import io.github.resilience4j.core.StringUtils;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

@Service
@Slf4j
@RequiredArgsConstructor
public class TokenConverterServiceImpl implements TokenConverterService {

    private final CustomerClient customerClient;

    @Override
    public String getServiceToken(String clientToken) {
        if (clientToken.startsWith("Bearer ")) {
            clientToken = clientToken.replace("Bearer ", "");
        }
        TokenConverterRequest request = TokenConverterRequest.builder()
                .externalToken(clientToken)
                .build();
        ResponseEntity<TokenConverterResponse> response = customerClient.tokenConverter(request);
        if (response != null && "200".equalsIgnoreCase(response.getBody().getCode())) {
            if (response.getBody().getData() != null
                    && StringUtils.isNotEmpty(response.getBody().getData().getInternalToken())) {
                return response.getBody().getData().getInternalToken();
            }
        }
        return null;
    }
}
