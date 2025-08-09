package com.org.product_catalog.exception;



import java.util.stream.Collectors;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import com.org.product_catalog.metrics.EndpointMetricsService;
import com.org.product_catalog.model.ApiResponseModel;

@ControllerAdvice
public class GlobalExceptionHandler {

	 private final EndpointMetricsService metricsService;
	 public GlobalExceptionHandler(EndpointMetricsService metricsService) {
		 this.metricsService=metricsService;
	 }
	 
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ApiResponseModel<String>> handleValidationException(MethodArgumentNotValidException ex) {
        String errorMessages = ex.getBindingResult().getFieldErrors()
                .stream()
                .map(err -> err.getField() + ": " + err.getDefaultMessage())
                .collect(Collectors.joining(", "));
        return ResponseEntity.badRequest().body(ApiResponseModel.failure("Validation failed "+ errorMessages));
    }

    // Handle IllegalArgumentException
    @ExceptionHandler(IllegalArgumentException.class)
    public ResponseEntity<ApiResponseModel<String>> handleIllegalArgument(IllegalArgumentException ex) {
        return ResponseEntity.badRequest().body(ApiResponseModel.failure(ex.getMessage()));
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<ApiResponseModel<String>> handleException(Exception ex) {
        metricsService.incrementFailure(ex.getMessage());
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(ApiResponseModel.error("An error occurred: " + ex.getMessage()));
    }
}