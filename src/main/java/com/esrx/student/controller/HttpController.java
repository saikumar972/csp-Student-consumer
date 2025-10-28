package com.esrx.student.controller;


import com.esrx.student.client.HttpClientCall;
import com.esrx.student.client.WebClientApp;
import com.esrx.student.dto.WebClientResponse;
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
    @Autowired
    WebClientApp webClientApp;
    @GetMapping("/name/{name}")
    public ResponseEntity<?> getStudentByNameHttp(@PathVariable String name, @RequestHeader Map<String,String> httpHeaders){
        Object response=httpClientCall.getStudentByNameHttpClient(name,httpHeaders);
        return ResponseEntity.status(HttpStatus.OK).body(response);
    }

    @GetMapping("/webName/{name}")
    public ResponseEntity<?> getStudentByNameWebClient(@PathVariable String name,@RequestHeader Map<String, String> httpHeaders){
        WebClientResponse response=webClientApp.getStudentByName(name,httpHeaders);
        return ResponseEntity.status(response.getStatus()).body(response.getObject());
    }
}
