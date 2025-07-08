package com.esrx.student.ControllerExceptionHandling;

import org.apache.tomcat.util.digester.RulesBase;

public class InvalidInput extends RuntimeException {
    public InvalidInput(String message) {
        super(message);
    }
}
