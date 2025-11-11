package org.example.banktransfer.exception;

public class BusinessException extends RuntimeException {
    public BusinessException(String msg) { super(msg); }
}