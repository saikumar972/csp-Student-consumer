package com.esrx.student.resilience;

import com.esrx.student.client.RestTemplateClient;
import com.esrx.student.dto.StudentDto;
import io.github.resilience4j.circuitbreaker.CircuitBreaker;
import io.github.resilience4j.retry.Retry;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.HttpServerErrorException;

import java.util.function.Supplier;

@Service
@AllArgsConstructor
public class ResilienceClient {
   private final Retry customRetry;
   private final RestTemplateClient restTemplateClient;
   private final CircuitBreaker circuitBreaker;

    public StudentDto getStudentByNameRetry(String name) {
        Supplier<StudentDto> supplier = () -> restTemplateClient.getStudentByNameV2(name);

        // Wrap supplier with CircuitBreaker
        Supplier<StudentDto> circuitBreakerSupplier = CircuitBreaker
                .decorateSupplier(circuitBreaker, supplier);

        // Wrap supplier with Retry on top of CircuitBreaker
        Supplier<StudentDto> retrySupplier = Retry
                .decorateSupplier(customRetry, circuitBreakerSupplier);

        try {
            return retrySupplier.get();
        } catch (Exception e) {
            return studentRetry(name, e);
        }
    }


    public StudentDto studentRetry(String name, Throwable t) {
        // 🚫 Prevent fallback for 4xx
        if (t instanceof HttpClientErrorException) {
            throw (HttpClientErrorException)t; // return same 4xx
        }
        else if (t instanceof HttpServerErrorException) {
            throw (HttpServerErrorException)t; // return same 4xx
        }
        // ✅ Only fallback for real server failures
        System.out.println("Custom Retry fallback executed due to: " + t.getMessage());
        StudentDto fallBackStudent = new StudentDto();
        fallBackStudent.setId(0L);
        fallBackStudent.setName("fallback-failed");
        return fallBackStudent;
    }

}
