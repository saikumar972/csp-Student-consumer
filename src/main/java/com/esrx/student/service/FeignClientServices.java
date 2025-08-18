package com.esrx.student.service;

import com.esrx.student.client.FeignClient;
import com.esrx.student.dto.StudentDto;
import com.esrx.student.dto.StudentInput;
import io.github.resilience4j.bulkhead.annotation.Bulkhead;
import io.github.resilience4j.circuitbreaker.annotation.CircuitBreaker;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

@Service
public class FeignClientServices {
    @Autowired
    FeignClient feignClient;

    ExecutorService executorService= Executors.newFixedThreadPool(4);

    public StudentDto addStudent(StudentDto studentDto){
        return feignClient.addStudent(studentDto).getBody();
    }


    public StudentDto  updateStudentDetails(StudentDto studentDto){
        return feignClient.updateStudentDetails(studentDto).getBody();
    }

    public List<StudentDto> studentList(){
        return feignClient.studentList().getBody();
    }


    public String deleteStudentById(Long id){
        return feignClient.deleteStudentById(id).getBody();
    }


    public StudentDto getStudentDetailsByIdAndName(StudentInput studentInput){
        return feignClient.getStudentDetailsByIdAndName(studentInput).getBody();
    }

    //circuit breaker
    @CircuitBreaker(name = "studentService", fallbackMethod = "failedName")
    public StudentDto getStudentByName(String name){
        return feignClient.getStudentByName(name).getBody();
    }

    public StudentDto failedName(String name,Throwable t){
        StudentDto fallBackStudent=new StudentDto();
        fallBackStudent.setId(0L);
        fallBackStudent.setName(name);
        fallBackStudent.setFees(100);
        System.out.println("FallBack invoked exception message is "+t.getMessage());
        return fallBackStudent;
    }

    //bulkhead testing
    @Bulkhead(name = "studentBulkHead",type = Bulkhead.Type.SEMAPHORE,fallbackMethod = "failed")
    public StudentDto getStudentDetailsById(Long id){
        return feignClient.getStudentDetailsById(id).getBody();
    }

    public StudentDto failed(Long id,Throwable t){
        StudentDto fallBackStudent=new StudentDto();
        fallBackStudent.setId(id);
        fallBackStudent.setName("fallback");
        fallBackStudent.setFees(100);
        System.out.println("FallBack invoked exception message is "+t.getMessage());
        return fallBackStudent;
    }

    public void test(){
        ExecutorService executor = Executors.newFixedThreadPool(4);
        for (int i = 0; i < 4; i++) {
            executor.submit(() -> {
                StudentDto dto = getStudentDetailsById(1L);
                System.out.println(dto.getName());
            });
        }
    }

}
