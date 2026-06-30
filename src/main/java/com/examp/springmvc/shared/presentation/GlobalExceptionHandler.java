package com.examp.springmvc.shared.presentation;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.validation.BindException;
import org.springframework.web.bind.MissingServletRequestParameterException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;

@ControllerAdvice
public class GlobalExceptionHandler {

    private static final Logger LOG = LoggerFactory.getLogger(GlobalExceptionHandler.class);

    /**
     * Handles bad request binding, validation, and argument type mismatch exceptions.
     * Logs the error at WARN level and returns the 400 error page.
     */
    @ExceptionHandler({
        BindException.class,
        MethodArgumentTypeMismatchException.class,
        MissingServletRequestParameterException.class,
        IllegalArgumentException.class
    })
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public String handleBadRequest(Exception ex) {
        LOG.warn("Bad request exception intercepted: {} - {}", ex.getClass().getSimpleName(), ex.getMessage());
        return "error/400";
    }

    /**
     * Handles all other unhandled exceptions (internal server errors).
     * Logs the full stack trace at ERROR level and returns the 500 error page.
     */
    @ExceptionHandler(Exception.class)
    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    public String handleInternalServerError(Exception ex) {
        LOG.error("Unhandled exception occurred during request execution: ", ex);
        return "error/500";
    }
}
