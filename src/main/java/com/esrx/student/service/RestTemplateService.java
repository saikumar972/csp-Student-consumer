package com.esrx.student.service;

import com.esrx.student.ControllerExceptionHandling.CustomStudentException;
import com.esrx.student.dto.StudentDto;
import com.esrx.student.dto.StudentInput;
import io.github.resilience4j.ratelimiter.RequestNotPermitted;
import io.github.resilience4j.ratelimiter.annotation.RateLimiter;
import io.github.resilience4j.retry.annotation.Retry;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.util.List;
import com.esrx.student.client.RestTemplateClient;
import org.springframework.web.client.HttpClientErrorException;

@Service
public class RestTemplateService {
    @Autowired
    RestTemplateClient restTemplateClient;

    public StudentDto createStudent(StudentDto studentDto) {
        return restTemplateClient.createStudent(studentDto);
    }

    public List<StudentDto> studentDtoList() {
        return restTemplateClient.studentDtoList();
    }

    public StudentDto getStudentByIdAndName(StudentInput studentInput) {
        return restTemplateClient.getStudentByIdAndName(studentInput);
    }

    //RateLimiter
    @RateLimiter(name = "studentService", fallbackMethod = "failed")
    public StudentDto getStudentById(Long id) {
        return restTemplateClient.getStudentById(id);
    }

    public StudentDto failed(Long id, Throwable t) {
        //if we use retry we have to handle exception here only not the try catch block in client call
        //if the too many req triggered the controller goes to fallback and executed the default student which written below
        //for server side errors also fallback method only triggered and we cannot write like ignore the exception in property file
        //so always handle exception in fallback method only
        System.out.println("fallback method executed");
        if (t instanceof HttpClientErrorException exception) {
            throw new CustomStudentException(exception.getResponseBodyAsString());
        } else if (t instanceof RequestNotPermitted ex) {
            throw ex;
        } else {
            StudentDto fallBackStudent = new StudentDto();
            fallBackStudent.setName("fallback");
            fallBackStudent.setId(id);
            fallBackStudent.setFees(0);
            return fallBackStudent;
        }
    }

    //Retry testing
    @Retry(name = "studentService", fallbackMethod = "studentRetry")
    public StudentDto getStudentByName(String name) {
        System.out.println("retried");
        return restTemplateClient.getStudentByName(name);
    }

    //We ignore the HttpClientExceptions and CustomStudentExceptions in the application properties
    public StudentDto studentRetry(String name, Throwable t) {
        System.out.println(t.getMessage());
        System.out.println("fallback executed");
        StudentDto fallBackStudent = new StudentDto();
        fallBackStudent.setName(name);
        fallBackStudent.setId(0L);
        fallBackStudent.setName("failed");
        return fallBackStudent;
    }
}
