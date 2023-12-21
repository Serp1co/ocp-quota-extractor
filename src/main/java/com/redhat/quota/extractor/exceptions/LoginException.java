package com.redhat.quota.extractor.exceptions;

import lombok.Getter;

@Getter
public class LoginException extends RuntimeException {

    final static String MESSAGE = "Login failed";

    LoginException(Exception e) {
        super(MESSAGE, e);
    }

    LoginException() {
        super(MESSAGE);
    }

    LoginException(String message) {
        super(message);
    }

    LoginException(String message, Exception e) {
        super(message, e);
    }


}
