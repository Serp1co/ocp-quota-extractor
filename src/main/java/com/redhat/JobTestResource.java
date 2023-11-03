package com.redhat;

import com.redhat.remote.LoginService;
import io.fabric8.kubernetes.client.Config;
import io.fabric8.kubernetes.client.ConfigBuilder;
import io.fabric8.kubernetes.client.KubernetesClient;
import io.fabric8.openshift.client.OpenShiftClient;
import io.fabric8.kubernetes.client.KubernetesClientBuilder;
import jakarta.inject.Inject;
import jakarta.ws.rs.POST;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.WebApplicationException;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import lombok.extern.java.Log;
import org.eclipse.microprofile.rest.client.inject.RestClient;

import javax.naming.LinkLoopException;

@Path("/job")
@Log
public class JobTestResource {

    @Inject
    JobRunner jobRunner;

    @Inject
    @RestClient
    LoginService loginService;
    @POST
    @Produces(MediaType.TEXT_PLAIN)
    public String hello() {
        String resp = "";
        String token = "";
        loginService.login("openshift-challenging-client", "token");
        String tokenString = LoginService.redirectLocation.get();
        for(String s : tokenString.split("&")) {
            if(s.contains("access_token")) token = s.split("access_token=")[1];
        };
        Config config = new ConfigBuilder().withMasterUrl("https://api.crc.testing:6443")
                .withDisableHostnameVerification(true)
                .withOauthToken(token)
                .build();
        log.info("client conf = " + config.getUsername());
        try (KubernetesClient generic_kube_client = new KubernetesClientBuilder().withConfig(config).build()) {
            OpenShiftClient ocp_client = generic_kube_client.adapt(OpenShiftClient.class);
            log.info("current user = " + ocp_client.currentUser().toString());
            resp = ocp_client.oAuthClients().list().toString();
            log.info(resp);
        }
        return resp;
    }

}
