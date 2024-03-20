package com.optimagrowth.license.utils;

import feign.RequestInterceptor;
import feign.RequestTemplate;

public class FeignClientInterceptor implements RequestInterceptor {

    @Override
    public void apply(RequestTemplate template) {
        // interceptor logic
        template.header(UserContext.CORRELATION_ID, UserContextHolder.getContext().getCorrelationId());
        template.header(UserContext.AUTH_TOKEN, UserContextHolder.getContext().getAuthToken());
        template.header(UserContext.AUTHORIZATION, UserContextHolder.getContext().getAuthorization());
    }
}