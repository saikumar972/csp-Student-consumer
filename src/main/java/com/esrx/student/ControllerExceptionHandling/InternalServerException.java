package com.esrx.student.ControllerExceptionHandling;

public class InternalServerException extends RuntimeException {
    public InternalServerException(String message) {
        super(message);
    }
}
