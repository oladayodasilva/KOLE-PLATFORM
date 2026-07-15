package com.kole.platform.common.web;

import org.slf4j.MDC;

public final class RequestContext {

    public static final String REQUEST_ID_KEY = "requestId";
    public static final String REQUEST_ID_HEADER = "X-Request-ID";

    private RequestContext() {
    }

    public static String requestId() {
        String requestId = MDC.get(REQUEST_ID_KEY);
        return requestId == null ? "unknown" : requestId;
    }
}