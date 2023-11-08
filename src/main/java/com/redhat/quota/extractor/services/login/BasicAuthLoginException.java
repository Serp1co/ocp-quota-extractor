package com.redhat.quota.extractor.services.login;

import lombok.Getter;

@Getter
public class BasicAuthLoginException extends Exception {

    final static String MESSAGE = "Login failed";

    public BasicAuthLoginException() {
        super(MESSAGE);
    }

    public BasicAuthLoginException(String message) {
        super(message);
    }

    public BasicAuthLoginException(Exception e) {
        super(MESSAGE, e);
    }

    public BasicAuthLoginException(String message, Exception e) {
        super(message, e);
    }

    public static class BasicAuthLoginConfigurationException extends BasicAuthLoginException {

        final static String MESSAGE = "BasicAuth Login activated but not configured properly";

        public BasicAuthLoginConfigurationException() {
            super(MESSAGE);
        }

    }

    @Getter
    public static class AuthTokenParseExceptionBasicAuth extends BasicAuthLoginException {

        final static String MESSAGE = "OAuth Token parsing failed";

        public AuthTokenParseExceptionBasicAuth() {
            super(MESSAGE);
        }

        public AuthTokenParseExceptionBasicAuth(Exception e) {
            super(MESSAGE, e);
        }
    }

    public static class AuthTokenNotReceivedExceptionBasicAuth extends BasicAuthLoginException {
        final static String MESSAGE = "OAuth Token is empty";

        public AuthTokenNotReceivedExceptionBasicAuth() {
            super(MESSAGE);
        }

    }

}