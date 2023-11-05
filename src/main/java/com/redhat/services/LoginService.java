package com.redhat.services;

import com.redhat.exception.ApplicationException;
import io.quarkus.rest.client.reactive.ClientRedirectHandler;
import io.smallrye.common.annotation.Blocking;
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
import org.eclipse.microprofile.rest.client.inject.RegisterRestClient;

import java.net.URI;
import java.util.Optional;

import static com.redhat.utils.ApiToEntity.getBasicAuthString;

@ApplicationScoped
@Log
public class LoginService {

    @Inject
    LoginConfigs authConfigs;

    OcpAuthClient buildLoginClient(String uri) {
        return RestClientBuilder.newBuilder()
                .baseUri(URI.create(uri))
                .build(OcpAuthClient.class);
    }

    String getToken(String tokenString) throws ApplicationException {
        Optional<String> token = Optional.empty();
        try {
            for (String s : tokenString.split("&")) {
                if (s.contains("access_token")) token = Optional.of(s.split("access_token=")[1]);
            }
        } catch (Exception e) {
            throw new ApplicationException.AuthTokenParseException(tokenString, e);
        }
        return token.orElseThrow(ApplicationException.AuthTokenNotReceivedException::new);
    }

    @Blocking
    public String doLogin() throws ApplicationException {
        OcpAuthClient ocpAuthClient = buildLoginClient(authConfigs.url());
        String basicAuthString =
                getBasicAuthString(authConfigs.credentials().username(), authConfigs.credentials().password());
        try (Response redirectionResponse =
                     ocpAuthClient.login(authConfigs.clientId(), authConfigs.responseType(), basicAuthString)) {
            return getToken(redirectionResponse.getLocation().toString());
        } catch (Exception e) {
            throw new ApplicationException("Generic Error", e);
        }
    }


    @ConfigMapping(prefix = "extractor.login")
    interface LoginConfigs {
        String url();

        String clientId();

        String responseType();

        BasicAuthConfigs credentials();

        interface BasicAuthConfigs {
            String password();

            String username();
        }

    }

    @Path("/oauth")
    @RegisterRestClient(configKey = "ocp-auth")
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
