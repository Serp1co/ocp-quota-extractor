package com.redhat.exception;

import lombok.Getter;

public class ApplicationException extends Exception {

    public ApplicationException(String message) {
        super(message);
    }

    public ApplicationException(String message, Exception e) {
        super(message, e);
    }

    @Getter
    public static class AuthTokenParseException extends ApplicationException {
        final String tokenString;

        public AuthTokenParseException(String tokenString) {
            super("OAuth Token parsing failed, failed uri string: " + tokenString);
            this.tokenString = tokenString;
        }

        public AuthTokenParseException(String tokenString, Exception e) {
            super("OAuth Token parsing failed, failed uri string: " + tokenString, e);
            this.tokenString = tokenString;
        }
    }

    public static class AuthTokenNotReceivedException extends ApplicationException {
        public AuthTokenNotReceivedException() {
            super("OAuth Token not present");
        }

    }

}
