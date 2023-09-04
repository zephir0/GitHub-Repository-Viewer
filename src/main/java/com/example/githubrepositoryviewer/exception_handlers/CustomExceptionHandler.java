package com.example.githubrepositoryviewer.exception_handlers;

import com.example.githubrepositoryviewer.ErrorResponse;
import org.springframework.http.HttpStatus;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.client.HttpClientErrorException;

import static org.springframework.http.HttpStatus.NOT_FOUND;
import static org.springframework.http.HttpStatus.UNAUTHORIZED;

@ControllerAdvice
public class CustomExceptionHandler {
    @ExceptionHandler(HttpClientErrorException.class)
    public ResponseEntity<ErrorResponse> handleHttpClientErrorException(HttpClientErrorException ex) {
        HttpStatusCode statusCode = ex.getStatusCode();
        if (statusCode.equals(NOT_FOUND)) {
            return ResponseEntity.status(NOT_FOUND).body(new ErrorResponse(NOT_FOUND.value(), "User profile was not found."));
        } else if (statusCode.equals(UNAUTHORIZED)) {
            return ResponseEntity.status(UNAUTHORIZED).body(new ErrorResponse(UNAUTHORIZED.value(), "GitHub api key is not correct."));
        }
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(new ErrorResponse(HttpStatus.INTERNAL_SERVER_ERROR.value(), "An unknown error occurred."));
    }

}
