package com.esrx.student.ControllerExceptionHandling;

public class CustomStudentException extends RuntimeException {
    public CustomStudentException(String message){
        super(message);
    }
}
