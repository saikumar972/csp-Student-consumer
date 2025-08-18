package com.esrx.student.client;

import com.esrx.student.ControllerExceptionHandling.CustomStudentException;
import com.esrx.student.dto.StudentDto;
import com.esrx.student.dto.StudentInput;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestTemplate;
import java.nio.charset.StandardCharsets;
import java.util.Base64;
import java.util.List;

@Service
public class RestTemplateClient {
    @Autowired
    RestTemplate restTemplate;
    @Value("${student.endpoint}")
    private String studentUrl;

    public StudentDto createStudent(StudentDto studentDto) {
        HttpEntity<StudentDto> httpEntity = new HttpEntity<>(studentDto);
        try {
            ResponseEntity<StudentDto> createStudent = restTemplate.exchange(
                    studentUrl + "/add",
                    HttpMethod.POST,
                    httpEntity,
                    StudentDto.class
            );
            return createStudent.getBody();
        } catch (HttpClientErrorException errorException) {
            throw new CustomStudentException(errorException.getResponseBodyAsString());
        } catch (Exception e) {
            throw new RuntimeException("Unable to reach student service: " + e.getMessage());
        }
    }

    public List<StudentDto> studentDtoList() {
        ResponseEntity<List<StudentDto>> studentResponse = restTemplate.exchange
                (studentUrl + "/all",
                        HttpMethod.GET,
                        null,
                        new ParameterizedTypeReference<>() {
                        }
                );
        return studentResponse.getBody();
    }

    public StudentDto getStudentByIdAndName(StudentInput studentInput) {
        HttpEntity<StudentInput> httpEntity = new HttpEntity<>(studentInput);
        try {
            ResponseEntity<StudentDto> studentResponse = restTemplate.exchange(
                    studentUrl + "/fetch",
                    HttpMethod.POST,
                    httpEntity,
                    StudentDto.class
            );
            return studentResponse.getBody();
        } catch (HttpClientErrorException errorException) {
            throw new CustomStudentException(errorException.getResponseBodyAsString());
        } catch (Exception e) {
            throw new RuntimeException("Backend returned 500: " + e.getMessage());
        }
    }

    //Rate limiter
    public StudentDto getStudentById(Long id) {
        return restTemplate.exchange(
                studentUrl + "/id/" + id,
                HttpMethod.GET,
                null,
                StudentDto.class
        ).getBody();
    }

    //Retry
    public StudentDto getStudentByName(String name) {
        try {
            ResponseEntity<StudentDto> studentResponse = restTemplate.exchange(
                    studentUrl + "/name/" + name,
                    HttpMethod.GET,
                    null,
                    StudentDto.class
            );
            return studentResponse.getBody();
        } catch (HttpClientErrorException errorException) {
            throw new CustomStudentException(errorException.getResponseBodyAsString());
        } catch (Exception e) {
            throw new RuntimeException("Backend returned 500: " + e.getMessage());
        }
    }

    private HttpEntity<StudentInput> getHttpEntity(StudentInput studentInput) {
        String userName = "student";
        String password = "student";
        String encodedString = Base64.getEncoder().encodeToString((userName + ":" + password).getBytes(StandardCharsets.UTF_8));
        String authenticated = "Basic " + encodedString;
        HttpHeaders httpHeaders = new HttpHeaders();
        httpHeaders.add("Authorization", authenticated);
        httpHeaders.add("Content-Type", "application/json");
        return new HttpEntity<>(studentInput, httpHeaders);
    }

}
