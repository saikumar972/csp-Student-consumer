package com.esrx.student.ControllerExceptionHandling;

public class InvalidIdException extends RuntimeException {
    public InvalidIdException(String message){
        super(message);
    }
}
