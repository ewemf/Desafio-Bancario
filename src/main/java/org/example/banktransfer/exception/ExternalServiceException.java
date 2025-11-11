package org.example.banktransfer.exception;

public class ExternalServiceException extends RuntimeException {
    public ExternalServiceException(String msg) { super(msg); }
}