package com.esrx.student.service;

import com.esrx.student.dto.StudentDto;
import com.esrx.student.dto.StudentInput;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.util.List;
import com.esrx.student.client.RestTemplateClient;
@Service
public class RestTemplateService {
  @Autowired
  RestTemplateClient restTemplateClient;

    public StudentDto createStudent(StudentDto studentDto){
        return restTemplateClient.createStudent(studentDto);
    }

    public List<StudentDto> studentDtoList(){
        return restTemplateClient.studentDtoList();
    }

    public StudentDto getStudentById(Long id){
        return restTemplateClient.getStudentById(id);
    }

    public StudentDto getStudentByIdAndName(StudentInput studentInput){
        return restTemplateClient.getStudentByIdAndName(studentInput);
    }
}
