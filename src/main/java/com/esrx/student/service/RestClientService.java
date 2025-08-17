package com.esrx.student.service;

import com.esrx.student.ControllerExceptionHandling.CustomStudentException;
import com.esrx.student.client.RestServiceClient;
import com.esrx.student.dto.StudentDto;
import com.esrx.student.dto.StudentInput;
import io.github.resilience4j.bulkhead.BulkheadFullException;
import io.github.resilience4j.bulkhead.annotation.Bulkhead;
import io.github.resilience4j.ratelimiter.RequestNotPermitted;
import io.github.resilience4j.ratelimiter.annotation.RateLimiter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.client.HttpClientErrorException;

import java.util.List;
import java.util.concurrent.CompletableFuture;

@Service
public class RestClientService {
   @Autowired
   RestServiceClient restServiceClient;

    public StudentDto addStudent(StudentDto studentDto){
        return restServiceClient.addStudent(studentDto);
    }

    public List<StudentDto> studentDtoList(){
        return restServiceClient.studentDtoList();
    }

    public StudentDto getStudentByIdAndName(StudentInput studentInput) {
       return restServiceClient.getStudentByIdAndName(studentInput);
    }

    public String deleteStudentById(int id){
        return restServiceClient.deleteStudentById(id);
    }

    @Bulkhead(name = "threadPoolBulkHeadTest",type = Bulkhead.Type.THREADPOOL,fallbackMethod = "failed")
    public CompletableFuture<StudentDto> getStudentById(Long id) {
        return CompletableFuture.completedFuture(restServiceClient.getStudentById(id));
    }

    public CompletableFuture<StudentDto> failed(Long id,Throwable t){
        if (t instanceof BulkheadFullException) {
            // Only handle Bulkhead rejections
            StudentDto fallBackStudent = new StudentDto();
            fallBackStudent.setFees(0);
            fallBackStudent.setId(id);
            fallBackStudent.setName("Fallback");
            return CompletableFuture.completedFuture(fallBackStudent);
        }else if(t instanceof HttpClientErrorException exception){
            System.out.println("handled server side exception");
            CompletableFuture<StudentDto> future=new CompletableFuture<>();
            future.completeExceptionally(new CustomStudentException(exception.getResponseBodyAsString()));
            return future;
        }else if (t instanceof RuntimeException) {
            // Propagate other runtime exceptions
            CompletableFuture<StudentDto> future = new CompletableFuture<>();
            future.completeExceptionally(t);
            return future;
        } else {
            // Wrap checked exceptions
            CompletableFuture<StudentDto> future = new CompletableFuture<>();
            future.completeExceptionally(new RuntimeException(t));
            return future;
        }
    }

    @RateLimiter(name="studentName",fallbackMethod = "failedRate")
    public StudentDto getStudentByName(String name){
        return restServiceClient.getStudentByName(name);
    }

    public StudentDto failedRate(String name,Throwable t){
        System.out.println("fallback method executed");
        if(t instanceof HttpClientErrorException exception){
            throw new CustomStudentException(exception.getResponseBodyAsString());
        }else if(t instanceof RequestNotPermitted ex){
            throw ex;
        }
        else{
            StudentDto fallBackStudent=new StudentDto();
            fallBackStudent.setName(name);
            fallBackStudent.setId(0L);
            fallBackStudent.setFees(0);
            return fallBackStudent;
        }
    }

}
