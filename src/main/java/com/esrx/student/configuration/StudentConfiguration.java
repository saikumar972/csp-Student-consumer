package com.esrx.student.configuration;

import io.github.resilience4j.circuitbreaker.CircuitBreakerRegistry;
import jakarta.annotation.PostConstruct;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.EnableAspectJAutoProxy;
import org.springframework.web.client.RestClient;
import org.springframework.web.client.RestTemplate;

@Configuration
@EnableAspectJAutoProxy
public class StudentConfiguration {
    @Bean(name="restTemplate")
    public RestTemplate restTemplate() {
       return new RestTemplate();
    }

    @Bean
    public RestClient restClient(){
        return RestClient.create();
    }
    private final CircuitBreakerRegistry circuitBreakerRegistry;

    public StudentConfiguration(CircuitBreakerRegistry circuitBreakerRegistry) {
        this.circuitBreakerRegistry = circuitBreakerRegistry;
    }

    @PostConstruct
    public void logCircuitBreakerState() {
        circuitBreakerRegistry.getAllCircuitBreakers()
                .forEach(cb -> cb.getEventPublisher().onStateTransition(
                        event -> System.out.println("CircuitBreaker '" + event.getCircuitBreakerName()
                                + "' changed from " + event.getStateTransition().getFromState()
                                + " to " + event.getStateTransition().getToState())
                ));
    }
}
