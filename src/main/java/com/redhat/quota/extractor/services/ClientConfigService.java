package com.redhat.quota.extractor.services;

import io.fabric8.kubernetes.client.Config;
import io.fabric8.kubernetes.client.ConfigBuilder;
import io.smallrye.config.ConfigMapping;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;

import java.util.Optional;
import java.util.Set;
import java.util.stream.Stream;

@ApplicationScoped
public class ClientConfigService {
    @Inject
    OcpBasicAuthLoginService loginService;

    @Inject
    ExtractorClientConfig extractorClientConfig;

    Stream<Config> getConfigsForClusters() {
        return extractorClientConfig.clusters().stream().parallel().map(clusterUrl -> {
            ConfigBuilder cf = new ConfigBuilder()
                    .withAutoConfigure(false)
                    .withMasterUrl(clusterUrl)
                    .withOauthToken(
                            loginService.login(
                                    clusterUrl,
                                    extractorClientConfig.clientId(),
                                    extractorClientConfig.username(),
                                    extractorClientConfig.password()
                            )
                    );
            return extractorClientConfig.ssl().orElse(true) ?
                    cf.build() : cf.withTrustCerts(true).withDisableHostnameVerification(true).build();
        });
    }

    @ConfigMapping(prefix = "extractor.client")
    interface ExtractorClientConfig {
        Set<String> clusters();
        String clientId();
        String username();
        String password();
        Optional<Boolean> ssl();
    }

}
