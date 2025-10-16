package com.esrx.student.resilience;

import io.github.resilience4j.circuitbreaker.CircuitBreaker;
import io.github.resilience4j.circuitbreaker.CircuitBreakerConfig;
import io.github.resilience4j.circuitbreaker.CircuitBreakerRegistry;
import io.github.resilience4j.core.IntervalFunction;
import io.github.resilience4j.retry.Retry;
import io.github.resilience4j.retry.RetryConfig;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.HttpServerErrorException;

import java.net.SocketTimeoutException;
import java.time.Duration;

@Configuration
public class ResilienceConfiguration {
    private final Duration duration=Duration.ofSeconds(5);
    IntervalFunction fixedBackOff = IntervalFunction.of(duration);

    @Bean(name = "customRetry")
    public Retry customRetry(){
        RetryConfig retryConfig = RetryConfig.custom()
                .maxAttempts(3)
                .intervalFunction(fixedBackOff)
                .retryOnException(ex -> {
                    if (ex instanceof HttpClientErrorException httpException) {
                        int status = httpException.getStatusCode().value();
                        return status == 408 || status == 429; // ✅ 4xx retry
                    }
                    return false;
                })
                .build();
        Retry retry= Retry.of("customRetry",retryConfig);
        // ✅ Add Retry Event Listeners
        retry.getEventPublisher()
                .onRetry(event -> System.out.println("🔁 Retry Attempt: " + event.getNumberOfRetryAttempts()
                        + " | Reason: " + event.getLastThrowable().getMessage()))
                .onSuccess(event -> System.out.println("✅ Retry Successful after attempts: "
                        + event.getNumberOfRetryAttempts()))
                .onError(event -> System.out.println("❌ Retry failed even after max attempts: "
                        + event.getNumberOfRetryAttempts()));
        return retry;
    }

    @Bean(name = "circuitBreaker")
    public CircuitBreaker circuitBreaker(){
        CircuitBreakerConfig circuitBreakerConfig = CircuitBreakerConfig.custom()
                .slidingWindowType(CircuitBreakerConfig.SlidingWindowType.COUNT_BASED)
                .slidingWindowSize(10)
                .minimumNumberOfCalls(5)
                .failureRateThreshold(50)
                .waitDurationInOpenState(Duration.ofSeconds(10))
                .permittedNumberOfCallsInHalfOpenState(3)
                .automaticTransitionFromOpenToHalfOpenEnabled(true)
                .recordExceptions(
                        HttpServerErrorException.class,
                        SocketTimeoutException.class,
                        HttpClientErrorException.BadRequest.class
                )
                .build();
        CircuitBreakerRegistry registry = CircuitBreakerRegistry.of(circuitBreakerConfig);
        CircuitBreaker circuitBreaker = registry.circuitBreaker("studentService", circuitBreakerConfig);

        // Add event listeners
        circuitBreaker.getEventPublisher()
                .onStateTransition(event -> System.out.println("CircuitBreaker state changed to " + event.getStateTransition()))
                .onCallNotPermitted(event -> System.out.println("Call not permitted as CircuitBreaker is open"))
                .onError(event -> System.out.println("CircuitBreaker recorded a failure: " + event.getThrowable().toString()))
                .onSuccess(event -> System.out.println("CircuitBreaker recorded a success."))
                .onReset(event -> System.out.println("CircuitBreaker reset."))
                .onIgnoredError(event -> System.out.println("CircuitBreaker ignored error: " + event.getThrowable().toString()));

        return circuitBreaker;
    }

}
