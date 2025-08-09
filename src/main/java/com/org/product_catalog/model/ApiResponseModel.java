package com.org.product_catalog.model;

import java.time.LocalDateTime;

public class ApiResponseModel<T> {
    private boolean success;
    private String message;
    private T data;
    private LocalDateTime timestamp;

    public ApiResponseModel(boolean success, String message, T data) {
        this.success = success;
        this.message = message;
        this.data = data;
        this.timestamp = LocalDateTime.now();
        
    }

    // Static helper methods
    public static <T> ApiResponseModel<T> success(String message, T data) {
        return new ApiResponseModel<>(true, message, data);
    }

    public static <T> ApiResponseModel<T> error(String message) {
        return new ApiResponseModel<>(false, message, null);
    }

    // Getters
    public boolean isSuccess() { return success; }
    public String getMessage() { return message; }
    public T getData() { return data; }
    public LocalDateTime getTimestamp() { return timestamp; }

    public static <T> ApiResponseModel<T> failure(String message) {
        return new ApiResponseModel<>(false, message, null);
    }
}
