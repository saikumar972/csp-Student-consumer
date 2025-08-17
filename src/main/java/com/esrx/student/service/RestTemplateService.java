package com.esrx.student.service;

import com.esrx.student.ControllerExceptionHandling.CustomStudentException;
import com.esrx.student.dto.StudentDto;
import com.esrx.student.dto.StudentInput;
import io.github.resilience4j.ratelimiter.RequestNotPermitted;
import io.github.resilience4j.ratelimiter.annotation.RateLimiter;
import io.github.resilience4j.retry.annotation.Retry;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Fallback;
import org.springframework.stereotype.Service;
import java.util.List;
import com.esrx.student.client.RestTemplateClient;
import org.springframework.web.client.HttpClientErrorException;

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

    public StudentDto getStudentByIdAndName(StudentInput studentInput){
        return restTemplateClient.getStudentByIdAndName(studentInput);
    }

    @RateLimiter(name = "studentService",fallbackMethod = "failed")
    public StudentDto getStudentById(Long id){
        return restTemplateClient.getStudentById(id);
    }

    public StudentDto failed(Long id,Throwable t){
        //we can omit the throwable coz we handle the exception through try/catch in client call
        if (t instanceof RequestNotPermitted) {
            // Rate limiter was exceeded
            System.out.println("Rate limiter triggered for Student ID: " + id);
            StudentDto fallbackDto = new StudentDto();
            fallbackDto.setId(id);
            fallbackDto.setName("Fallback Student");
            return fallbackDto;
        }
        else if(t instanceof HttpClientErrorException exception){
            System.out.println("executed the custom exception");
            throw new CustomStudentException(exception.getResponseBodyAsString());
        }
        else {
            // Rethrow other exceptions (like HTTP 400 errors)
            if (t instanceof RuntimeException exception) throw exception;
            throw new RuntimeException(t);
        }
    }

    @Retry(name = "studentService", fallbackMethod = "studentRetry")
    public StudentDto getStudentByName(String name){
        System.out.println("retried");
        return restTemplateClient.getStudentByName(name);
    }

    public StudentDto studentRetry(String name,Throwable t){
        System.out.println(t.getMessage());
        System.out.println("fallback executed");
        StudentDto fallBackStudent=new StudentDto();
        fallBackStudent.setName(name);
        fallBackStudent.setId(0L);
        fallBackStudent.setName("failed");
        return fallBackStudent;
    }

}
