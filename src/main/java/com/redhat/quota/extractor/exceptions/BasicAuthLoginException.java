package com.redhat.quota.extractor.exceptions;

import lombok.Getter;

public class BasicAuthLoginException extends LoginException {

    public BasicAuthLoginException(Exception e) {
        super(MESSAGE, e);
    }

    public static class BasicAuthLoginConfigurationException extends LoginException {

        final static String MESSAGE = "BasicAuth Login activated but not configured properly";

        public BasicAuthLoginConfigurationException() {
            super(MESSAGE);
        }

    }

    @Getter
    public static class AuthTokenParseException extends LoginException {

        final static String MESSAGE = "OAuth Token parsing failed";

        public AuthTokenParseException(Exception e) {
            super(MESSAGE, e);
        }

    }

    public static class AuthTokenNotReceivedException extends LoginException {
        final static String MESSAGE = "OAuth Token is empty";

        public AuthTokenNotReceivedException() {
            super(MESSAGE);
        }

    }

    public static class AuthDiscoveryEndpointException extends LoginException {
        final static String MESSAGE = "Error parsing discovery response";

        public AuthDiscoveryEndpointException() {
            super(MESSAGE);
        }

    }

}
