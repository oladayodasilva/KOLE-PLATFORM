package com.kole.platform.common.security;

import tools.jackson.databind.json.JsonMapper;
import com.kole.platform.common.api.ApiError;
import com.kole.platform.common.api.ApiResponse;
import com.kole.platform.common.exception.ErrorCode;
import com.kole.platform.common.web.RequestContext;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import org.springframework.http.MediaType;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.AuthenticationEntryPoint;
import org.springframework.stereotype.Component;

@Component
public class RestAuthenticationEntryPoint
    implements AuthenticationEntryPoint {

    private final JsonMapper jsonMapper;

    public RestAuthenticationEntryPoint(
        JsonMapper jsonMapper
    ) {
        this.jsonMapper = jsonMapper;
    }

    @Override
    public void commence(
        HttpServletRequest request,
        HttpServletResponse response,
        AuthenticationException exception
    ) throws IOException {

        response.setStatus(
            HttpServletResponse.SC_UNAUTHORIZED
        );

        response.setContentType(
            MediaType.APPLICATION_JSON_VALUE
        );

        jsonMapper.writeValue(
            response.getOutputStream(),
            ApiResponse.failure(
                "Authentication is required",
                ApiError.of(
                    ErrorCode
                        .AUTHENTICATION_REQUIRED
                        .code()
                ),
                RequestContext.requestId()
            )
        );
    }
}