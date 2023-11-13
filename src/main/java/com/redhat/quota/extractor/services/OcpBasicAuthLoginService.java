package com.redhat.quota.extractor.services;

import com.redhat.quota.extractor.exceptions.LoginException;
import com.redhat.quota.extractor.exceptions.BasicAuthLoginException;
import io.quarkus.rest.client.reactive.ClientRedirectHandler;
import io.smallrye.config.ConfigMapping;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.ws.rs.HeaderParam;
import jakarta.ws.rs.POST;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.QueryParam;
import jakarta.ws.rs.core.Response;
import lombok.extern.java.Log;
import org.eclipse.microprofile.rest.client.RestClientBuilder;

import java.net.URI;
import java.util.Base64;
import java.util.Optional;

import com.redhat.quota.extractor.exceptions.BasicAuthLoginException.BasicAuthLoginConfigurationException;
import com.redhat.quota.extractor.exceptions.BasicAuthLoginException.AuthTokenParseException;
import com.redhat.quota.extractor.exceptions.BasicAuthLoginException.AuthTokenNotReceivedException;

@ApplicationScoped
@Log
public class OcpBasicAuthLoginService {

    @Inject
    LoginConfigs loginConfigs;

    /**
     * logs in with basic auth and returns the oauth token to propagate
     *
     * @return the auth token
     * @throws LoginException failed login or failed oauth token
     */
    public String login() throws LoginException {
        LoginConfigs.BasicAuthClientConfigs basicAuthClientConfigs =
                loginConfigs.basicAuth().orElseThrow(BasicAuthLoginConfigurationException::new);
        return doLoginAndReturnToken(
                        basicAuthClientConfigs.url(),
                        basicAuthClientConfigs.clientId(),
                        basicAuthClientConfigs.responseType(),
                        basicAuthClientConfigs.credentials().username(),
                        basicAuthClientConfigs.credentials().password());
    }

    static OcpAuthClient buildLoginClient(String uri) {
        return RestClientBuilder.newBuilder()
                .baseUri(URI.create(uri))
                .build(OcpAuthClient.class);
    }

    static String getBasicAuthString(String username, String password) {
        return "Basic " + Base64.getEncoder()
                .encodeToString((username + ":" + password).getBytes());
    }

    static String doLoginAndReturnToken(String url, String clientId,
                                        String responseType, String username,
                                        String password) throws LoginException {
        OcpAuthClient ocpAuthClient = buildLoginClient(url);
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
     * Login rest client interface, to use programmatically
     */
    @Path("/oauth")
    interface OcpAuthClient {

        /**
         * always follow redirects (not only on GET requests)
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
         * @param client_id the client id
         * @param response_type the response type
         * @param auth the authorization header
         * @return the login response
         */
        @Path("/authorize")
        @POST
        Response login(@QueryParam("client_id") String client_id,
                       @QueryParam("response_type") String response_type,
                       @HeaderParam("Authorization") String auth);

    }

    @ConfigMapping(prefix = "extractor.client.login.auth")
    interface LoginConfigs {

        Optional<BasicAuthClientConfigs> basicAuth();

        interface BasicAuthClientConfigs {
            String url();

            String clientId();

            String responseType();

            BasicAuthCredentialsConfigs credentials();

            interface BasicAuthCredentialsConfigs {
                String password();

                String username();
            }
        }
    }

}
