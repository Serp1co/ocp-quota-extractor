package com.redhat;

public class ApplicationException extends RuntimeException {
    public ApplicationException(Exception e) {
        super(e);
    }

}
