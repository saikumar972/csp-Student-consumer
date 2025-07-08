package com.esrx.student.service;

import com.esrx.student.ControllerExceptionHandling.InternalServerException;
import com.esrx.student.ControllerExceptionHandling.InvalidIdException;
import com.esrx.student.ControllerExceptionHandling.InvalidInput;
import com.esrx.student.dto.StudentDto;
import com.esrx.student.dto.StudentInput;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.ResourceAccessException;
import org.springframework.web.client.RestTemplate;

import java.util.List;

@Service
public class RestTemplateService {
    @Autowired
    RestTemplate restTemplate;
    @Value("${student.endpoint}")
    private String studentUrl;

    public List<StudentDto> studentDtoList(){
        ResponseEntity<List<StudentDto>> studentResponse=restTemplate.exchange
                (studentUrl+"/all",
                        HttpMethod.GET,
                        null,
                        new ParameterizedTypeReference<List<StudentDto>>(){}
                );
        return studentResponse.getBody();
    }

    public StudentDto getStudentById(Long id){
        try{
            ResponseEntity<StudentDto> studentResponse=restTemplate.exchange(
                    studentUrl+"/id/"+id,
                    HttpMethod.GET,
                    null,
                    StudentDto.class
            );
            return studentResponse.getBody();
        }catch (HttpClientErrorException.BadRequest e){
                throw new InvalidIdException("The given id "+id+" is invalidId");
        }catch (ResourceAccessException e) {
            throw new InternalServerException("Unable to reach student service: " + e.getMessage());
        }
    }

    public StudentDto getStudentByIdAndName(StudentInput studentInput){
        HttpEntity<StudentInput> httpEntity=new HttpEntity<>(studentInput);
        try{
            ResponseEntity<StudentDto> studentResponse=restTemplate.exchange(
                    studentUrl+"/fetch",
                    HttpMethod.POST,
                    httpEntity,
                    StudentDto.class
            );
            return studentResponse.getBody();
        }catch (HttpClientErrorException.BadRequest e){
                throw new InvalidInput("The input is not valid");
        }catch (Exception e){
            throw new InternalServerException("Backend returned 500: " + e.getMessage());
        }
    }

}
