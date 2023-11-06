package com.redhat.quota.extractor.exception;

import lombok.Getter;


public class ApplicationException extends Exception {

    ApplicationException() {super();}
    public ApplicationException(String message) {
        super(message);
    }

    public ApplicationException(String message, Exception e) {
        super(message, e);
    }

    @Getter
    public static class LoginException extends ApplicationException {

        final String loginUri;

        public LoginException(String uri) {
            super("Login failed for URI="+uri);
            this.loginUri = uri;
        }

        public LoginException(String uri, Exception e) {
            super("Login failed for URI="+uri, e);
            this.loginUri = uri;
        }

    }

    @Getter
    public static class AuthTokenParseException extends LoginException {
        final String tokenString;

        public AuthTokenParseException(String tokenString) {
            super("OAuth Token parsing failed, failed uri string=" + tokenString);
            this.tokenString = tokenString;
        }

        public AuthTokenParseException(String tokenString, Exception e) {
            super("OAuth Token parsing failed, failed uri string=" + tokenString, e);
            this.tokenString = tokenString;
        }
    }

    public static class AuthTokenNotReceivedException extends LoginException {
        public AuthTokenNotReceivedException() {
            super("OAuth Token is empty");
        }

    }

}
