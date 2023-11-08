package com.redhat.quota.extractor.services.collector;

import com.redhat.quota.extractor.services.login.BasicAuthLoginException;
import com.redhat.quota.extractor.services.login.BasicAuthLoginService;
import io.fabric8.kubernetes.client.Config;
import io.fabric8.kubernetes.client.ConfigBuilder;
import io.fabric8.kubernetes.client.KubernetesClient;
import io.fabric8.kubernetes.client.KubernetesClientBuilder;
import io.fabric8.openshift.client.OpenShiftClient;
import io.smallrye.config.ConfigMapping;
import jakarta.enterprise.inject.Produces;
import jakarta.inject.Inject;
import jakarta.inject.Singleton;
import lombok.extern.java.Log;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@Singleton
@Log
public class OcpCollectorClientProvider {

    @Inject
    BasicAuthLoginService basicAuthLoginService;

    @Inject
    OcpQuotaClientConfigs ocpQuotaClientConfigs;

    @ConfigMapping(prefix = "extractor.client.quota")
    interface OcpQuotaClientConfigs {

        List<String> clustersUrl();

    }


    @Produces
    public Map<String, OpenShiftClient> ocpClients() throws BasicAuthLoginException {
        Map<String, OpenShiftClient> clusterClientsMap = new HashMap<>();
        for(String cluster : ocpQuotaClientConfigs.clustersUrl()) {
            Config ocpConfig = ocpConfigBuilder(cluster).build();
            try (KubernetesClient openShiftClient = new KubernetesClientBuilder()
                    .withConfig(ocpConfig)
                    .build()
            ) {
                clusterClientsMap.put(cluster, openShiftClient.adapt(OpenShiftClient.class));
            }
        }
        return clusterClientsMap;
    }

    ConfigBuilder ocpConfigBuilder(String cluster) throws BasicAuthLoginException {
        ConfigBuilder ocpConfigBuilder = new ConfigBuilder()
                .withMasterUrl(cluster)
                .withDisableHostnameVerification(true)
                .withTrustCerts(true);
        Optional<String> optionalToken = basicAuthLoginService.login();
        return optionalToken
                .map(ocpConfigBuilder::withOauthToken)
                .orElse(ocpConfigBuilder);
    }

}
