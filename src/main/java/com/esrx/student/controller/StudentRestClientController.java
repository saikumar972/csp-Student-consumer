package com.esrx.student.controller;

import com.esrx.student.dto.StudentDto;
import com.esrx.student.dto.StudentInput;
import com.esrx.student.service.RestClientService;
import lombok.SneakyThrows;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.List;

@RestController
@RequestMapping("/students")
public class StudentRestClientController {
    @Autowired
    RestClientService restClientService;
    @Value("${student.name}")
    String name;
    @PostMapping("/add")
    public ResponseEntity<StudentDto> addStudent(@RequestBody StudentDto studentDto){
        StudentDto student= restClientService.addStudent(studentDto);
        return ResponseEntity.status(HttpStatus.OK).body(student);
    }
    @GetMapping("/all")
    public ResponseEntity<List<StudentDto>> getStudents(){
        List<StudentDto> studentDtoList= restClientService.studentDtoList();
        System.out.println(name);
        return ResponseEntity.status(HttpStatus.OK).body(studentDtoList);
    }

    @PostMapping("/fetch")
    public ResponseEntity<StudentDto> getStudentByIdAndName(@RequestBody StudentInput studentInput){
        StudentDto studentDto= restClientService.getStudentByIdAndName(studentInput);
        return ResponseEntity.status(HttpStatus.OK).body(studentDto);
    }
    @DeleteMapping("/id/{id}")
    public ResponseEntity<String> deleteStudentById(@PathVariable int id){
        String status= restClientService.deleteStudentById(id);
        return ResponseEntity.status(HttpStatus.NO_CONTENT).body(status);
    }

    //ThreadPoolBulk head testing
    @GetMapping("/id/{id}")
    @SneakyThrows
    public ResponseEntity<StudentDto> getStudentById(@PathVariable Long id){
        StudentDto studentDto= restClientService.getStudentById(id).get();
        return ResponseEntity.status(HttpStatus.OK).body(studentDto);
    }

    //RateLimiter testing
    @GetMapping("/name/{name}")
    public ResponseEntity<StudentDto> getStudentByName(@PathVariable String name){
        StudentDto studentDto= restClientService.getStudentByName(name);
        return ResponseEntity.status(HttpStatus.OK).body(studentDto);
    }
}
