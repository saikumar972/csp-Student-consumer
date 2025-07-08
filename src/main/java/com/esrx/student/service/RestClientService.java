package com.esrx.student.service;

import com.esrx.student.dto.StudentDto;
import com.esrx.student.dto.StudentInput;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpEntity;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestClient;
import org.springframework.web.client.RestTemplate;

import java.util.List;

@Service
public class RestClientService {
    @Autowired
    RestClient restClient;
    @Value("${student.endpoint}")
    private String studentUrl;

    public List<StudentDto> studentDtoList(){
        List<StudentDto> studentList=
                restClient.get()
                        .uri(studentUrl+"/all")
                        .retrieve()
                        .body(new ParameterizedTypeReference<List<StudentDto>>() {});
        return studentList;
    }

    public StudentDto getStudentById(Long id) {
        StudentDto studentDto=restClient
                .get()
                .uri(studentUrl+"/id/"+id)
                .retrieve()
                .body(StudentDto.class);
        return  studentDto;
    }

    public StudentDto getStudentByIdAndName(StudentInput studentInput) {
        StudentDto studentDto=restClient
                .post()
                .uri(studentUrl+"/fetch")
                .body(studentInput)
                .retrieve()
                .body(StudentDto.class);
        return studentDto;
    }
}
