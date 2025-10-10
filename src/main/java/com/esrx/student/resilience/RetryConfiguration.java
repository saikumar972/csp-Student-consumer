package com.esrx.student.resilience;

import io.github.resilience4j.core.IntervalFunction;
import io.github.resilience4j.retry.Retry;
import io.github.resilience4j.retry.RetryConfig;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.HttpServerErrorException;

import java.time.Duration;

@Configuration
public class RetryConfiguration {
    IntervalFunction exponentialRandomBackOff=
            IntervalFunction.ofExponentialRandomBackoff(
                    Duration.ofSeconds(2),
                    2.0,
                    0.5
            );
    @Bean(name = "customRetry")
    public Retry customRetry(){
        RetryConfig retryConfig = RetryConfig.custom()
                .maxAttempts(5)
                .intervalFunction(exponentialRandomBackOff)
                .retryExceptions(
                        HttpServerErrorException.BadGateway.class,
                        HttpServerErrorException.GatewayTimeout.class,
                        HttpServerErrorException.ServiceUnavailable.class
                )
                .ignoreExceptions(
                        HttpClientErrorException.class,
                        com.esrx.student.ControllerExceptionHandling.CustomStudentException.class
                )
                .build();
        return Retry.of("customRetry",retryConfig);
    }
}
