package com.esrx.student.resilience;

import com.esrx.student.client.RestTemplateClient;
import com.esrx.student.dto.StudentDto;
import io.github.resilience4j.retry.Retry;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.function.Supplier;

@Service
@AllArgsConstructor
public class RetryClient {
   private final Retry customRetry;
   private final RestTemplateClient restTemplateClient;

   public StudentDto getStudentByNameRetry(String name){
       Supplier<StudentDto> retryableSupplier=Retry
               .decorateSupplier(customRetry,
                       ()->restTemplateClient.getStudentByName(name));
       try{
           return retryableSupplier.get();
       } catch (Exception e) {
           return studentRetry(name,e);
       }
   }

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
