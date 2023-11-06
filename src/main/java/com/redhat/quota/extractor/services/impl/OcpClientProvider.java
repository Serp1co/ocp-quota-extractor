package com.redhat.quota.extractor.services.impl;

import com.redhat.quota.extractor.exception.LoginException;
import io.fabric8.kubernetes.client.Config;
import io.fabric8.kubernetes.client.ConfigBuilder;
import io.fabric8.kubernetes.client.KubernetesClient;
import io.fabric8.kubernetes.client.KubernetesClientBuilder;
import io.fabric8.openshift.client.OpenShiftClient;
import io.smallrye.config.ConfigMapping;
import jakarta.ejb.Singleton;
import jakarta.enterprise.inject.Produces;
import jakarta.inject.Inject;
import lombok.extern.java.Log;

import java.util.List;
import java.util.Optional;

@Singleton
@Log
public class OcpClientProvider {

    @Inject
    ExtractorConfigs extractorConfigs;
    @Inject
    BasicAuthLoginService basicAuthLoginService;

    @Produces
    public OpenShiftClient ocpClient(String cluster) throws LoginException {
        Config ocpConfig = ocpConfigBuilder(cluster).build();
        try (KubernetesClient openShiftClient = new KubernetesClientBuilder()
                .withConfig(ocpConfig)
                .build()
        ) {
            return openShiftClient.adapt(OpenShiftClient.class);
        }
    }

    ConfigBuilder ocpConfigBuilder(String cluster) throws LoginException {
        ConfigBuilder ocpConfigBuilder = new ConfigBuilder()
                .withMasterUrl(cluster)
                .withDisableHostnameVerification(true)
                .withTrustCerts(true);
        Optional<String> optionalToken = basicAuthLoginService.login();
        return optionalToken
                .map(ocpConfigBuilder::withOauthToken)
                .orElse(ocpConfigBuilder);
    }

    @ConfigMapping(prefix = "extractor")
    interface ExtractorConfigs {

        List<String> clustersUrl();

    }
}
