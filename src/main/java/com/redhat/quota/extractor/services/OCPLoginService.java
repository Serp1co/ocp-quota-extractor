package com.redhat.quota.extractor.services;

import com.redhat.quota.extractor.exception.ApplicationException.AuthTokenNotReceivedException;
import com.redhat.quota.extractor.exception.ApplicationException.AuthTokenParseException;
import com.redhat.quota.extractor.exception.ApplicationException.LoginException;
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
import java.util.Optional;

import static com.redhat.quota.extractor.utils.ApiToEntity.getBasicAuthString;


@ApplicationScoped
@Log
public class OCPLoginService {

    @Inject
    LoginConfigs loginConfigs;

    public String login() throws LoginException {
        return doLogin(
                loginConfigs.url(),
                loginConfigs.clientId(),
                loginConfigs.responseType(),
                loginConfigs.credentials().username(),
                loginConfigs.credentials().password()
        );
    }

    OcpAuthClient buildLoginClient(String uri) {
        return RestClientBuilder.newBuilder()
                .baseUri(URI.create(uri))
                .build(OcpAuthClient.class);
    }

    String getToken(String tokenString) throws LoginException {
        Optional<String> token = Optional.empty();
        try {
            for (String s : tokenString.split("&")) {
                if (s.contains("access_token")) token = Optional.of(s.split("access_token=")[1]);
            }
        } catch (Exception e) {
            throw new AuthTokenParseException(tokenString, e);
        }
        return token.orElseThrow(AuthTokenNotReceivedException::new);
    }

    String doLogin(String url, String clientId, String responseType, String username, String password) throws LoginException {
        OcpAuthClient ocpAuthClient = buildLoginClient(url);
        String basicAuthString =
                getBasicAuthString(username, password);
        try (Response redirectionResponse =
                     ocpAuthClient.login(clientId, responseType, basicAuthString)) {
            return getToken(redirectionResponse.getLocation().toString());
        } catch (Exception e) {
            throw new LoginException("Generic Error", e);
        }
    }

    @ConfigMapping(prefix = "extractor.login")
    interface LoginConfigs {
        String url();

        String clientId();

        String responseType();

        boolean useSaAuth();

        BasicAuthConfigs credentials();

        interface BasicAuthConfigs {
            String password();

            String username();
        }

    }

    /**
     * Login rest client interface, to use programmatically
     */
    @Path("/oauth")
    interface OcpAuthClient {

        @ClientRedirectHandler
        static URI alwaysRedirect(Response response) {
            if (Response.Status.Family.familyOf(response.getStatus()) == Response.Status.Family.REDIRECTION) {
                return response.getLocation();
            }
            return null;
        }

        @Path("/authorize")
        @POST
        Response login(@QueryParam("client_id") String client_id,
                       @QueryParam("response_type") String response_type,
                       @HeaderParam("Authorization") String auth);

    }

}
