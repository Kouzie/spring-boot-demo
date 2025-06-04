package com.example.mapper.config;

import com.example.mapper.response.ErrorCode;
import com.example.mapper.response.ErrorResponseDto;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.exc.InvalidTypeIdException;
import com.fasterxml.jackson.databind.exc.MismatchedInputException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.MessageSource;
import org.springframework.context.i18n.LocaleContextHolder;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.validation.FieldError;
import org.springframework.web.HttpRequestMethodNotSupportedException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.servlet.resource.NoResourceFoundException;

import java.util.stream.Collectors;

@Slf4j
@RestControllerAdvice
@RequiredArgsConstructor
public class ExceptionAdvice {

    private final MessageSource messageSource;

    @ExceptionHandler(value = HttpMessageNotReadableException.class)
    public ResponseEntity<ErrorResponseDto> handleException(HttpMessageNotReadableException e) {
        String message = messageSource.getMessage("error.BAD_REQUEST", null, LocaleContextHolder.getLocale());
        String description = getDescription(e);
        return new ResponseEntity<>(
                new ErrorResponseDto(
                        ErrorCode.BAD_REQUEST.getCode(),
                        ErrorCode.BAD_REQUEST.getError(),
                        description + " " + message
                ), HttpStatus.BAD_REQUEST
        );
    }

    @ExceptionHandler(value = MethodArgumentNotValidException.class)
    public ResponseEntity<ErrorResponseDto> handleException(MethodArgumentNotValidException e) {
        FieldError error = e.getFieldError();
        String message = null;
        if (error != null) {
            message = error.getDefaultMessage();
        }
        return new ResponseEntity<>(
                new ErrorResponseDto(
                        ErrorCode.VALIDATION_ERROR.getCode(),
                        ErrorCode.VALIDATION_ERROR.getError(),
                        message != null ? message : messageSource.getMessage("error.VALIDATION_ERROR", null, LocaleContextHolder.getLocale())
                ), HttpStatus.BAD_REQUEST
        );
    }

    @ExceptionHandler(value = HttpRequestMethodNotSupportedException.class)
    public ResponseEntity<ErrorResponseDto> handleException(HttpRequestMethodNotSupportedException e) {
        return new ResponseEntity<>(new ErrorResponseDto(
                ErrorCode.METHOD_NOT_ALLOW.getCode(),
                ErrorCode.METHOD_NOT_ALLOW.getError(),
                messageSource.getMessage("error.METHOD_NOT_ALLOW", null, LocaleContextHolder.getLocale())
        ), HttpStatus.NOT_FOUND);
    }

    @ExceptionHandler(value = NoResourceFoundException.class)
    public ResponseEntity<ErrorResponseDto> handleException(NoResourceFoundException e) {
        return new ResponseEntity<>(new ErrorResponseDto(
                ErrorCode.PAGE_NOT_FOUND.getCode(),
                ErrorCode.PAGE_NOT_FOUND.getError(),
                messageSource.getMessage("error.PAGE_NOT_FOUND", null, LocaleContextHolder.getLocale())
        ), HttpStatus.NOT_FOUND);
    }

    @ExceptionHandler(value = Exception.class)
    public ResponseEntity<ErrorResponseDto> handleException(Exception e) {
        log.info("error type:{}, local:{}", e.getClass().getSimpleName(), LocaleContextHolder.getLocale());

        return new ResponseEntity<>(new ErrorResponseDto(
                ErrorCode.SERVER_ERROR.getCode(),
                ErrorCode.SERVER_ERROR.getError(),
                messageSource.getMessage("error.SERVER_ERROR", null, LocaleContextHolder.getLocale())
        ), HttpStatus.NOT_FOUND);
    }

    public static String getDescription(Exception e) {
        Throwable cause = e.getCause();
        if (cause instanceof InvalidTypeIdException) {
            return getPathAsString((InvalidTypeIdException) cause);
        } else if (cause instanceof MismatchedInputException) {
            return getPathAsString((MismatchedInputException) cause);
        } else {
            return "";
        }
    }

    private static String getPathAsString(JsonMappingException exception) {
        return exception.getPath().stream()
                .map(ref -> ref.getFieldName() != null ? ref.getFieldName() : "")
                .collect(Collectors.joining());
    }
}
