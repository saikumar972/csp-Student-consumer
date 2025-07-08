package com.esrx.student.ControllerExceptionHandling;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
public class CustomExceptionHandling {
    @ExceptionHandler(InvalidIdException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public String invalidInput(InvalidIdException exception){
        return exception.getMessage();
    }
    @ExceptionHandler(InternalServerException.class)
    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    public String invalidInput(InternalServerException exception){
        return exception.getMessage();
    }
    @ExceptionHandler(InvalidInput.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public String invalidInput(InvalidInput exception){
        return exception.getMessage();
    }
}
