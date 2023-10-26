package com.redhat;

import jakarta.annotation.Priority;
import jakarta.ws.rs.Priorities;
import jakarta.ws.rs.client.ClientRequestContext;
import jakarta.ws.rs.client.ClientRequestFilter;
import jakarta.ws.rs.core.HttpHeaders;

import java.io.IOException;
import java.util.Base64;

@Priority(Priorities.AUTHENTICATION)
public class AuthFilter implements ClientRequestFilter {

        @Override
        public void filter(ClientRequestContext requestContext) throws IOException {
            requestContext.getHeaders().add(HttpHeaders.AUTHORIZATION, getAccessToken());
        }

        private String getAccessToken() {
            return "Basic " + Base64.getEncoder().encodeToString("someuser:somepass".getBytes());
        }

}