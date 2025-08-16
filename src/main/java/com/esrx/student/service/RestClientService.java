package com.esrx.student.service;

import com.esrx.student.client.RestClient;
import com.esrx.student.dto.StudentDto;
import com.esrx.student.dto.StudentInput;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.util.List;

@Service
public class RestClientService {
   @Autowired
   RestClient restClient;

    public StudentDto addStudent(StudentDto studentDto){
        return restClient.addStudent(studentDto);
    }

    public List<StudentDto> studentDtoList(){
        return restClient.studentDtoList();
    }

    public StudentDto getStudentById(Long id) {
        return restClient.getStudentById(id);
    }

    public StudentDto getStudentByIdAndName(StudentInput studentInput) {
       return restClient.getStudentByIdAndName(studentInput);
    }

    public String deleteStudentById(int id){
        return restClient.deleteStudentById(id);
    }

}
