package com.kole.platform.common.api;

import com.fasterxml.jackson.annotation.JsonInclude;
import java.util.Map;

@JsonInclude(JsonInclude.Include.NON_EMPTY)
public record ApiError(
    String code,
    Map<String, String> fields
) {

    public static ApiError of(String code) {
        return new ApiError(code, Map.of());
    }

    public static ApiError of(
        String code,
        Map<String, String> fields
    ) {
        return new ApiError(code, Map.copyOf(fields));
    }
}