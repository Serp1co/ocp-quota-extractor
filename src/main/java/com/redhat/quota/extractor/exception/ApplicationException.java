package com.redhat.quota.extractor.exception;

public class ApplicationException extends Exception {

    ApplicationException(String message) {
        super(message);
    }

    ApplicationException(String message, Exception e) {
        super(message, e);
    }

}
