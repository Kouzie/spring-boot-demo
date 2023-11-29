package com.example.mapper.config;

import com.example.mapper.response.ErrorResponseDto;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import javax.servlet.http.HttpServletRequest;

@Slf4j
@RestControllerAdvice
public class ExceptionAdvice {

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ErrorResponseDto> methodValidException(MethodArgumentNotValidException e, HttpServletRequest request) {
        log.error("methodValidException invoked, method:{}, url:{}, trace:{}", request.getMethod(), request.getRequestURI(), e.getLocalizedMessage());
        log.error("exceptLine:{}", e.getStackTrace()[0].getLineNumber());
        ErrorResponseDto ret = makeMethodValidExceptionResponse(e.getBindingResult());
        return new ResponseEntity<>(ret, HttpStatus.BAD_REQUEST);

    }

    private static final String VALIDATION_ERROR_TYPE = "ValidationError"; // 400

    private ErrorResponseDto makeMethodValidExceptionResponse(BindingResult bindingResult) {
        //에러가 있다면
        if (bindingResult.hasErrors()) {
            //DTO에 설정한 meaasge값을 가져온다
            return new ErrorResponseDto(bindingResult.getFieldError().getDefaultMessage(), HttpStatus.BAD_REQUEST.value());
        } else {
            return new ErrorResponseDto(
                    VALIDATION_ERROR_TYPE,
                    HttpStatus.BAD_REQUEST.value());
        }
    }
}
