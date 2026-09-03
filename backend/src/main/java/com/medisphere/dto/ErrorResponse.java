package com.medisphere.dto;

public record ErrorResponse(
        boolean success,
        String error,
        String message
) {
    public static ErrorResponse of(String error, String message) {
        return new ErrorResponse(false, error, message);
    }
}
