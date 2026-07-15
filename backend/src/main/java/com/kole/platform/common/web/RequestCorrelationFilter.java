package com.kole.platform.common.web;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.UUID;
import org.slf4j.MDC;
import org.springframework.core.Ordered;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;
import org.springframework.web.filter.OncePerRequestFilter;

@Component
@Order(Ordered.HIGHEST_PRECEDENCE)
public class RequestCorrelationFilter extends OncePerRequestFilter {

    @Override
    protected void doFilterInternal(
        HttpServletRequest request,
        HttpServletResponse response,
        FilterChain filterChain
    ) throws ServletException, IOException {

        String requestId = resolveRequestId(request);

        MDC.put(RequestContext.REQUEST_ID_KEY, requestId);
        response.setHeader(
            RequestContext.REQUEST_ID_HEADER,
            requestId
        );

        try {
            filterChain.doFilter(request, response);
        } finally {
            MDC.remove(RequestContext.REQUEST_ID_KEY);
        }
    }

    private String resolveRequestId(HttpServletRequest request) {
        String suppliedRequestId =
            request.getHeader(RequestContext.REQUEST_ID_HEADER);

        if (StringUtils.hasText(suppliedRequestId)
            && suppliedRequestId.length() <= 100) {
            return suppliedRequestId;
        }

        return UUID.randomUUID().toString();
    }
}