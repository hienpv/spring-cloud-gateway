package com.msb.ibs.edge.service.configs;

import feign.Logger;
import feign.Request;
import feign.Response;
import lombok.extern.slf4j.Slf4j;

import java.io.IOException;
import java.nio.charset.StandardCharsets;

import static feign.Logger.Level.HEADERS;

@Slf4j
public class FeignRequestResponseLogging extends Logger {

    private static final int BODY_MAX_LENGTH = 1000;

    @Override
    protected void logRequest(String configKey, Level logLevel, Request request) {

        if (logLevel.ordinal() >= HEADERS.ordinal()) {
            super.logRequest(configKey, logLevel, request);
        } else {
            int bodyLength = 0;
            String body = null;
            if (request.body() != null) {
                bodyLength = request.body().length;
                if (bodyLength <= BODY_MAX_LENGTH) {
                    body = new String(request.body(), StandardCharsets.UTF_8);
                }
            }
            log(configKey, "---> %s %s %s HTTP/1.1 (%s-byte body) ", request.httpMethod().name(),
                    request.url(), body, bodyLength);
        }
    }

    @Override
    protected Response logAndRebufferResponse(String configKey, Level logLevel, Response response,
                                              long elapsedTime)
            throws IOException {
        if (logLevel.ordinal() >= HEADERS.ordinal()) {
            super.logAndRebufferResponse(configKey, logLevel, response, elapsedTime);
        } else {
            int status = response.status();
            Request request = response.request();
            log(configKey, "<--- %s %s HTTP/1.1 %s (%sms) ", request.httpMethod().name(), request.url(),
                    status, elapsedTime);
        }
        return response;
    }

    @Override
    protected void log(String configKey, String format, Object... args) {
        log.info(format(configKey, format, args));
    }

    protected String format(String configKey, String format, Object... args) {
        return String.format(methodTag(configKey) + format, args);
    }
}
