package com.esrx.student.ControllerExceptionHandling;

import com.esrx.student.dto.StudentErrorDto;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.github.resilience4j.ratelimiter.RequestNotPermitted;
import lombok.SneakyThrows;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
public class RestTemplateExceptionHandler {
    @ExceptionHandler(CustomStudentException.class)
    @SneakyThrows
    public ResponseEntity<StudentErrorDto> invalidInput(CustomStudentException exception){
       ObjectMapper objectMapper=new ObjectMapper();
       StudentErrorDto studentErrorDto=objectMapper.readValue(exception.getMessage(),StudentErrorDto.class);
       int status=studentErrorDto.getResponseCode();
       return switch (status){
           case 400->ResponseEntity.status(HttpStatus.BAD_REQUEST).body(studentErrorDto);
           case 404  ->ResponseEntity.status(HttpStatus.NOT_FOUND).body(studentErrorDto);
           case 502->ResponseEntity.status(HttpStatus.GATEWAY_TIMEOUT).body(studentErrorDto);
           default -> ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(studentErrorDto);
        };
       //return ResponseEntity.status(HttpStatus.NOT_FOUND).body(studentErrorDto);
    }

    @ExceptionHandler(InternalServerException.class)
    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    public String internalServerErrorMethod(InternalServerException exception){
        return exception.getMessage();
    }

    //rate limiter
    @ExceptionHandler(RequestNotPermitted.class)
    @ResponseStatus(HttpStatus.TOO_MANY_REQUESTS)
    public String requestNotPermitted(RequestNotPermitted exception){
        return exception.getMessage();
    }

}
