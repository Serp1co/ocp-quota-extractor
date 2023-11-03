package com.redhat.remote;

import io.quarkus.rest.client.reactive.ClientRedirectHandler;
import jakarta.ws.rs.POST;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.QueryParam;
import jakarta.ws.rs.core.Response;
import org.eclipse.microprofile.rest.client.annotation.ClientHeaderParam;
import org.eclipse.microprofile.rest.client.inject.RegisterRestClient;

import java.net.URI;
import java.util.Base64;
import java.util.concurrent.atomic.AtomicReference;

@Path("/oauth")
@ClientHeaderParam(name = "Authorization", value = "{lookupAuth}")
@RegisterRestClient(configKey="ocp-auth")
public interface LoginService {

    AtomicReference<String> redirectLocation = new AtomicReference<>();

    @Path("/authorize")
    @POST
    Response login(@QueryParam("client_id") String client_id,
                          @QueryParam("response_type") String response_type);

    default String lookupAuth() {
        return "Basic " +
                Base64.getEncoder().encodeToString("kubeadmin:kubeadmin".getBytes());
    }

    @ClientRedirectHandler
    static URI alwaysRedirect(Response response) {
        if (Response.Status.Family.familyOf(response.getStatus()) == Response.Status.Family.REDIRECTION) {
            redirectLocation.set(response.getLocation().toString());
            return response.getLocation();
        }
        return null;
    }

}
