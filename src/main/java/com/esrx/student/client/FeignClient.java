package com.esrx.student.client;

import com.esrx.student.dto.StudentDto;
import com.esrx.student.dto.StudentInput;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.List;

@org.springframework.cloud.openfeign.FeignClient(name = "feign-client-service",url = "http://localhost:8000/student")
public interface FeignClient {
    @PostMapping("/add")
    ResponseEntity<StudentDto> addStudent(@RequestBody StudentDto studentDto);

    @PutMapping("/update")
    ResponseEntity<StudentDto>  updateStudentDetails(@RequestBody StudentDto studentDto);

    @GetMapping("/id/{id}")
    ResponseEntity<StudentDto> getStudentDetailsById(@PathVariable Long id);

    @GetMapping("/all")
    ResponseEntity<List<StudentDto>> studentList();

    @DeleteMapping("/id/{id}")
    ResponseEntity<String> deleteStudentById(@PathVariable Long id);

    @PostMapping("/fetch")
    ResponseEntity<StudentDto> getStudentDetailsByIdAndName(@RequestBody StudentInput studentInput);
}
