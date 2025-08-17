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
       return  feignClientServices.addStudent(studentDto);
    }

    @PutMapping("/update")
    public ResponseEntity<StudentDto>  updateStudentDetails(@RequestBody StudentDto studentDto){
        return feignClientServices.updateStudentDetails(studentDto);
    }

    @GetMapping("/id/{id}")
    public ResponseEntity<StudentDto> getStudentDetailsById(@PathVariable Long id){
        try { Thread.sleep(5000); } catch (InterruptedException ignored) {}
        StudentDto studentDto=feignClientServices.getStudentDetailsById(id);
        return ResponseEntity.status(HttpStatus.OK).body(studentDto);
    }
    //bulkhead
    @GetMapping("/test")
    public void bulkHeadTest(){
        feignClientServices.test();
    }

    @GetMapping("/all")
    public ResponseEntity<List<StudentDto>> studentList(){
        return feignClientServices.studentList();
    }

    @DeleteMapping("/id/{id}")
    public ResponseEntity<String> deleteStudentById(@PathVariable Long id){
        return feignClientServices.deleteStudentById(id);
    }

    @PostMapping("/fetch")
    public ResponseEntity<StudentDto> getStudentDetailsByIdAndName(@RequestBody StudentInput studentInput){
        return feignClientServices.getStudentDetailsByIdAndName(studentInput);
    }

    @GetMapping("/name/{name}")
    public ResponseEntity<StudentDto> getStudentByName(@PathVariable String name){
        StudentDto studentDto=feignClientServices.getStudentByName(name);
        return ResponseEntity.status(HttpStatus.OK).body(studentDto);
    }

}
