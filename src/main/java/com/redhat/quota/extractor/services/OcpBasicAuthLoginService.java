package com.redhat.quota.extractor.services;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.redhat.quota.extractor.exceptions.BasicAuthLoginException;
import com.redhat.quota.extractor.exceptions.BasicAuthLoginException.AuthTokenNotReceivedException;
import com.redhat.quota.extractor.exceptions.BasicAuthLoginException.AuthTokenParseException;
import com.redhat.quota.extractor.exceptions.LoginException;
import io.quarkus.rest.client.reactive.ClientRedirectHandler;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.ws.rs.*;
import jakarta.ws.rs.core.Response;
import lombok.extern.slf4j.Slf4j;
import org.eclipse.microprofile.rest.client.RestClientBuilder;

import java.net.URI;
import java.util.Base64;
import java.util.Map;
import java.util.Optional;

@ApplicationScoped
@Slf4j
public class OcpBasicAuthLoginService {

    static final String responseType = "token";

    @Inject
    ObjectMapper objectMapper;

    static OcpLoginAuthClient buildLoginClient(String uri) {
        return RestClientBuilder.newBuilder()
                .baseUri(URI.create(uri))
                .build(OcpLoginAuthClient.class);
    }

    static String getBasicAuthString(String username, String password) {
        return "Basic " + Base64.getEncoder()
                .encodeToString((username + ":" + password).getBytes());
    }

    static OcpDiscoveryAuthClient buildDiscoveryClient(String uri) {
        return RestClientBuilder.newBuilder()
                .baseUri(URI.create(uri))
                .build(OcpDiscoveryAuthClient.class);
    }

    static String getToken(String url, String clientId, String username, String password) throws LoginException {
        OcpLoginAuthClient ocpAuthClient = buildLoginClient(url);
        String basicAuthString =
                getBasicAuthString(username, password);
        try (Response redirectionResponse =
                     ocpAuthClient.login(clientId, responseType, basicAuthString)) {
            //get token from uri redirect
            return getTokenFromRedirect(redirectionResponse.getLocation().toString());
        } catch (Exception e) {
            throw new BasicAuthLoginException(e);
        }
    }

    static String getTokenFromRedirect(String tokenString) throws LoginException {
        Optional<String> token = Optional.empty();
        try {
            for (String s : tokenString.split("&")) {
                if (s.contains("access_token")) token = Optional.of(s.split("access_token=")[1]);
            }
        } catch (Exception e) {
            throw new AuthTokenParseException(e);
        }
        return token.orElseThrow(AuthTokenNotReceivedException::new);
    }

    /**
     * logs in with basic auth and returns the oauth token
     *
     * @return the auth token
     * @throws LoginException failed login or failed oauth token
     */
    public String login(String apiUrl, String client_id, String username, String psw) {
        OcpDiscoveryAuthClient ocpAuthClient = buildDiscoveryClient(apiUrl);
        String response = ocpAuthClient.wellKnown();
        try {
            Map<String, String> map = objectMapper.readValue(response, Map.class);
            String authUrl = map.get("authorization_endpoint");
            return getToken(authUrl, client_id, username, psw);
        } catch (JsonProcessingException e) {
            log.error("Error parsing discovery response");
            throw new RuntimeException(e);
        }
    }

    /**
     * Login rest client interface, to use programmatically
     */
    interface OcpLoginAuthClient {

        /**
         * always follow redirects (not only on GET requests)
         *
         * @param response the response
         * @return the uri redirection
         */
        @ClientRedirectHandler
        static URI alwaysRedirect(Response response) {
            if (Response.Status.Family.familyOf(response.getStatus()) == Response.Status.Family.REDIRECTION) {
                return response.getLocation();
            }
            return null;
        }

        /**
         * try to log in, returns the response with the token
         *
         * @param client_id     the client id
         * @param response_type the response type
         * @param auth          the authorization header
         * @return the login response
         */
        @POST
        Response login(@QueryParam("client_id") String client_id,
                       @QueryParam("response_type") String response_type,
                       @HeaderParam("Authorization") String auth);

    }

    /**
     * Login rest client interface, to use programmatically
     */
    interface OcpDiscoveryAuthClient {

        /**
         * try to get the wellknown endpoint for oicd discovery
         *
         * @return the login response
         */
        @Path("/.well-known/oauth-authorization-server")
        @GET
        String wellKnown();

    }

}
