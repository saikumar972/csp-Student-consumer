package com.esrx.student.resilience;

import com.esrx.student.client.RestTemplateClient;
import com.esrx.student.dto.StudentDto;
import io.github.resilience4j.retry.Retry;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.web.client.HttpClientErrorException;

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
        // 🚫 Prevent fallback for 4xx
        if (t instanceof HttpClientErrorException) {
            throw (HttpClientErrorException)t; // return same 4xx
        }
        // ✅ Only fallback for real server failures
        System.out.println("Custom Retry fallback executed due to: " + t.getMessage());
        StudentDto fallBackStudent = new StudentDto();
        fallBackStudent.setId(0L);
        fallBackStudent.setName("fallback-failed");
        return fallBackStudent;
    }

}
