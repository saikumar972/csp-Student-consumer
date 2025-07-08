package com.esrx.student.controller;

import com.esrx.student.dto.StudentDto;
import com.esrx.student.dto.StudentInput;
import com.esrx.student.service.RestTemplateService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.List;

@RestController
@RequestMapping("/student")
public class StudentConsumerController {
    @Autowired
    RestTemplateService restTemplateService;
    @Value("${student.name}")
    String name;
    @GetMapping("/all")
    public ResponseEntity<List<StudentDto>> getStudents(){
        List<StudentDto> studentDtoList= restTemplateService.studentDtoList();
        System.out.println(name);
        return ResponseEntity.status(HttpStatus.OK).body(studentDtoList);
    }
    @GetMapping("/id/{id}")
    public ResponseEntity<StudentDto> getStudentById(@PathVariable Long id){
        StudentDto studentDto= restTemplateService.getStudentById(id);
        return ResponseEntity.status(HttpStatus.OK).body(studentDto);
    }
    @PostMapping("/fetch")
    public ResponseEntity<StudentDto> getStudentByIdAndName(@RequestBody StudentInput studentInput){
        StudentDto studentDto= restTemplateService.getStudentByIdAndName(studentInput);
        return ResponseEntity.status(HttpStatus.OK).body(studentDto);
    }
}
