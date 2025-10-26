package com.esrx.student.ControllerExceptionHandling;

import com.esrx.student.controller.StudentFeignController;
import com.esrx.student.dto.StudentErrorDto;
import com.fasterxml.jackson.databind.ObjectMapper;
import feign.FeignException;
import lombok.SneakyThrows;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice(assignableTypes = {StudentFeignController.class})
public class FeignExceptionHandling {
    @ExceptionHandler(FeignException.BadRequest.class)
    @SneakyThrows
    public ResponseEntity<StudentErrorDto> invalidInput(FeignException.BadRequest exception){
        ObjectMapper objectMapper=new ObjectMapper();
        StudentErrorDto studentErrorDto=objectMapper.readValue(exception.contentUTF8(),StudentErrorDto.class);
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(studentErrorDto);
    }

    //we can write all the feign exception like below
    @ExceptionHandler(FeignException.class)
    @SneakyThrows
    public ResponseEntity<?> handlingAllExceptions(FeignException exception){
        ObjectMapper objectMapper=new ObjectMapper();
        StudentErrorDto studentErrorDto=objectMapper.readValue(exception.contentUTF8(),StudentErrorDto.class);
        return switch (exception.status()){
            case 404-> ResponseEntity.status(HttpStatus.NOT_FOUND).body(studentErrorDto);
            case 500-> ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(studentErrorDto);
            case 502-> ResponseEntity.status(HttpStatus.BAD_GATEWAY).body(studentErrorDto);
            default -> ResponseEntity.status(exception.status()).body(exception.contentUTF8());
        };
    }
}
