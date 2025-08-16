package com.esrx.student.service;

import com.esrx.student.client.FeignClient;
import com.esrx.student.dto.StudentDto;
import com.esrx.student.dto.StudentInput;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.PathVariable;

import java.util.List;

@Service
public class FeignClientServices {
    @Autowired
    FeignClient feignClient;

    public ResponseEntity<StudentDto> addStudent(StudentDto studentDto){
        return feignClient.addStudent(studentDto);
    }


    public ResponseEntity<StudentDto>  updateStudentDetails(StudentDto studentDto){
        return feignClient.updateStudentDetails(studentDto);
    }

    public ResponseEntity<StudentDto> getStudentDetailsById(@PathVariable Long id){
        return feignClient.getStudentDetailsById(id);
    }

    public ResponseEntity<List<StudentDto>> studentList(){
        return feignClient.studentList();
    }


    public ResponseEntity<String> deleteStudentById(@PathVariable Long id){
        return feignClient.deleteStudentById(id);
    }


    public ResponseEntity<StudentDto> getStudentDetailsByIdAndName(StudentInput studentInput){
        return feignClient.getStudentDetailsByIdAndName(studentInput);
    }
}
