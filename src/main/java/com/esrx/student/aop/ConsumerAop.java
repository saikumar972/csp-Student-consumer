package com.esrx.student.aop;

import com.esrx.student.ControllerExceptionHandling.CustomStudentException;
import lombok.extern.log4j.Log4j2;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.AfterThrowing;
import org.aspectj.lang.annotation.Aspect;
import org.springframework.stereotype.Component;
import org.springframework.web.client.HttpClientErrorException;

@Aspect
@Component
@Log4j2
public class ConsumerAop {
    @AfterThrowing(value = "execution(* com.esrx.student.service.RestClientService.deleteStudentById(..))",throwing = "exception")
    public void testingException(JoinPoint joinPoint, HttpClientErrorException exception){
        log.error("ConsumerAop :: Exception occurred at {}",joinPoint.getSignature().getName());
        log.error("ConsumerAop :: Exception message is {}",exception.getMessage());
        throw new CustomStudentException(exception.getResponseBodyAsString());
    }
}
