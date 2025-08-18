package com.esrx.student.controller;

import com.esrx.student.dto.StudentDto;
import com.esrx.student.dto.StudentInput;
import com.esrx.student.service.FeignClientServices;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.List;

@RestController
@RequestMapping("/feign")
public class StudentFeignController {
    @Autowired
    FeignClientServices feignClientServices;


    @PostMapping("/add")
    public ResponseEntity<StudentDto> addStudent(@RequestBody StudentDto studentDto){
       StudentDto student = feignClientServices.addStudent(studentDto);
       return ResponseEntity.status(HttpStatus.CREATED).body(student);
    }

    @PutMapping("/update")
    public ResponseEntity<StudentDto>  updateStudentDetails(@RequestBody StudentDto studentDto){
        StudentDto student =  feignClientServices.updateStudentDetails(studentDto);
        return ResponseEntity.status(HttpStatus.ACCEPTED).body(student);
    }

    @GetMapping("/all")
    public ResponseEntity<List<StudentDto>> studentList(){
        List<StudentDto> studentDtoList = feignClientServices.studentList();
        return ResponseEntity.status(HttpStatus.OK).body(studentDtoList);
    }

    @DeleteMapping("/id/{id}")
    public ResponseEntity<String> deleteStudentById(@PathVariable Long id){
        String deleteMessage = feignClientServices.deleteStudentById(id);
        return ResponseEntity.status(HttpStatus.NO_CONTENT).body(deleteMessage);
    }

    @PostMapping("/fetch")
    public ResponseEntity<StudentDto> getStudentDetailsByIdAndName(@RequestBody StudentInput studentInput){
        StudentDto student = feignClientServices.getStudentDetailsByIdAndName(studentInput);
        return ResponseEntity.status(HttpStatus.OK).body(student);
    }

    //circuit breaker test
    @GetMapping("/name/{name}")
    public ResponseEntity<StudentDto> getStudentByName(@PathVariable String name){
        StudentDto studentDto=feignClientServices.getStudentByName(name);
        return ResponseEntity.status(HttpStatus.OK).body(studentDto);
    }

    //bulkhead
    @GetMapping("/id/{id}")
    public ResponseEntity<StudentDto> getStudentDetailsById(@PathVariable Long id){
        try { Thread.sleep(5000); } catch (InterruptedException ignored) {}
        StudentDto studentDto=feignClientServices.getStudentDetailsById(id);
        return ResponseEntity.status(HttpStatus.OK).body(studentDto);
    }
    @GetMapping("/test")
    public void bulkHeadTest(){
        feignClientServices.test();
    }

}
