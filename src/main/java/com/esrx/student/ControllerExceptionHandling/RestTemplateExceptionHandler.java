package com.esrx.student.ControllerExceptionHandling;

import com.esrx.student.controller.HttpController;
import com.esrx.student.controller.StudentRestTemplateController;
import com.esrx.student.dto.StudentErrorDto;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.github.resilience4j.ratelimiter.RequestNotPermitted;
import lombok.SneakyThrows;
import org.springframework.http.HttpStatus;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.HttpServerErrorException;

@RestControllerAdvice(assignableTypes = {StudentRestTemplateController.class, HttpController.class})
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

    //rate limiter
    @ExceptionHandler(RequestNotPermitted.class)
    public ResponseEntity<String> requestNotPermitted(RequestNotPermitted exception) {
        System.out.println("Exception invoked because of too many requests coming");
        return ResponseEntity
                .status(HttpStatus.TOO_MANY_REQUESTS)
                .body("Request got denied because of " + exception.getMessage());
    }

    //rate limiter
    @ExceptionHandler(HttpClientErrorException.class)
    public ResponseEntity<String> clientExceptions(HttpClientErrorException exception) {
        System.out.println("Exception invoked because for client errors");
        HttpStatusCode httpStatusCode=exception.getStatusCode();
        return ResponseEntity
                .status(httpStatusCode)
                .body("Request got retried because of " + exception.getMessage());
    }

    @ExceptionHandler(HttpServerErrorException.class)
    public ResponseEntity<String> serverExceptions(HttpServerErrorException exception) {
        System.out.println("Exception invoked because for client errors");
        HttpStatusCode httpStatusCode=exception.getStatusCode();
        return ResponseEntity
                .status(httpStatusCode)
                .body("Request got circuitBreaker because of " + exception.getMessage());
    }

}
