package com.licenta.biomechanics_backend.exception;

public class PythonProcessingException extends RuntimeException {
    public PythonProcessingException(String message) {
        super(message);
    }

    public PythonProcessingException(String message, Throwable cause) {
        super(message, cause);
    }
}
