package com.esrx.student.controller;


import com.esrx.student.client.HttpClientCall;
import com.esrx.student.dto.StudentDto;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/http")
public class HttpController {
    @Autowired
    HttpClientCall httpClientCall;
    @GetMapping("/name/{name}")
    public ResponseEntity<?> getStudentByNameHttp(@PathVariable String name, @RequestHeader Map<String,String> httpHeaders){
        StudentDto studentDto=httpClientCall.getStudentByNameHttpClient(name,httpHeaders);
        return ResponseEntity.status(HttpStatus.OK).body(studentDto);
    }
}
