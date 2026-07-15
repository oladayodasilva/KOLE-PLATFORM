package com.kole.platform.common.security;

import com.kole.platform.common.api.ApiError;
import com.kole.platform.common.web.RequestContext;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.Map;
import org.springframework.http.MediaType;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.web.access.AccessDeniedHandler;
import org.springframework.stereotype.Component;
import tools.jackson.databind.json.JsonMapper;

@Component
public class RestAccessDeniedHandler
    implements AccessDeniedHandler {

    private final JsonMapper jsonMapper;

    public RestAccessDeniedHandler(
        JsonMapper jsonMapper
    ) {
        this.jsonMapper = jsonMapper;
    }

    @Override
    public void handle(
        HttpServletRequest request,
        HttpServletResponse response,
        AccessDeniedException exception
    ) throws IOException {

        response.setStatus(
            HttpServletResponse.SC_FORBIDDEN
        );

        response.setContentType(
            MediaType.APPLICATION_JSON_VALUE
        );

        response.setCharacterEncoding("UTF-8");

        String requestId = RequestContext.requestId();

        if (requestId != null && !requestId.isBlank()) {
            response.setHeader(
                "X-Request-ID",
                requestId
            );
        }

        ApiError error = new ApiError(
            "ACCESS_DENIED",
            Map.of(
                "message",
                "You do not have permission to access this resource",
                "path",
                request.getRequestURI()
            )
        );

        jsonMapper.writeValue(
            response.getOutputStream(),
            error
        );
    }
}