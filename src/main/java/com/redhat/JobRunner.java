package com.redhat;

import com.redhat.models.Namespaces;
import com.redhat.remote.LoginService;
import com.redhat.utils.ApiToEntity;
import io.fabric8.kubernetes.api.model.Namespace;
import io.fabric8.kubernetes.api.model.ObjectMeta;
import io.fabric8.kubernetes.client.Config;
import io.fabric8.kubernetes.client.ConfigBuilder;
import io.fabric8.kubernetes.client.KubernetesClient;
import io.fabric8.kubernetes.client.KubernetesClientBuilder;
import io.fabric8.openshift.client.OpenShiftClient;
import io.quarkus.scheduler.Scheduled;
import io.smallrye.common.annotation.Blocking;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.transaction.Transactional;
import jakarta.ws.rs.core.Response;
import lombok.extern.java.Log;
import org.eclipse.microprofile.rest.client.inject.RestClient;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collector;
import java.util.stream.Collectors;

@ApplicationScoped
@Log
public class JobRunner {

    String[] clusters = {"https://api.crc.testing:6443"};

    @Inject
    @RestClient
    LoginService loginService;

    @Transactional
    @Scheduled(cron = "${job.schedule.time: 0 0 10 * * ?}")
    void schedule() {
        doJob();
    }

    @Blocking
    public void doJob() {
        String token = doLogin().orElseThrow();
        for (String cluster: clusters) {
            Config config = buildOcpClientConfig(cluster, token);
            try (KubernetesClient generic_kube_client = new KubernetesClientBuilder().withConfig(config).build()) {
                try(OpenShiftClient ocp_client = generic_kube_client.adapt(OpenShiftClient.class)) {
                    List<Namespaces> namespacesList = getNamespacesFromApi(ocp_client, cluster);
                }
            }
        }
    }


    @Blocking
    Optional<String> doLogin() throws ApplicationException {
        Optional<String> token = Optional.empty();
        try (Response ignored = loginService.login("openshift-challenging-client", "token")) {
            String tokenString = LoginService.redirectLocation.get();
            for (String s : tokenString.split("&")) {
                if (s.contains("access_token")) token = Optional.of(s.split("access_token=")[1]);
            }
        } catch (Exception e) {
            throw new ApplicationException(e);
        }
        return token;
    }

    Config buildOcpClientConfig(final String cluster, final String oauthToken) {
        return new ConfigBuilder().withMasterUrl(cluster)
                .withDisableHostnameVerification(true)
                .withOauthToken(oauthToken)
                .build();
    }

    OpenShiftClient buildOcpClient(Config config) {
        try (KubernetesClient generic_kube_client = new KubernetesClientBuilder().withConfig(config).build()) {
            return generic_kube_client.adapt(OpenShiftClient.class);
        }
    }

    List<Namespaces> getNamespacesFromApi(OpenShiftClient ocp_client, String cluster) {
        return ocp_client.namespaces()
                .list()
                .getItems()
                .stream()
                .map(Namespace::getMetadata)
                .map(ObjectMeta::getName)
                .map(ns_name -> ApiToEntity.NAMESPACES(ns_name, cluster))
                .toList()
                //.forEach(ns -> ns.persist())
                ;
    }

}
