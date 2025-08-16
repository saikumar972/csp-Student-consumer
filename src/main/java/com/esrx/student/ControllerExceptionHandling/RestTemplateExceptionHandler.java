package com.esrx.student.ControllerExceptionHandling;

import com.esrx.student.dto.StudentErrorDto;
import com.fasterxml.jackson.databind.ObjectMapper;
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
       return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(studentErrorDto);
    }

    @ExceptionHandler(InternalServerException.class)
    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    public String internalServerErrorMethod(InternalServerException exception){
        return exception.getMessage();
    }

}
