package com.redhat.quota.extractor.services;

import io.fabric8.kubernetes.client.Config;
import io.fabric8.kubernetes.client.ConfigBuilder;
import io.smallrye.config.ConfigMapping;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import lombok.extern.slf4j.Slf4j;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Stream;

@ApplicationScoped
@Slf4j
public class ClientConfigService {
    @Inject
    OcpBasicAuthLoginService loginService;

    @Inject
    ExtractorClientConfig extractorClientConfig;

    Stream<Config> getConfigsForClusters() {
        List<Config> configs = new ArrayList<>();
        extractorClientConfig.clusters().stream().parallel().forEach(clusterUrl -> {
            try {
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
                configs.add(
                        extractorClientConfig.ssl().orElse(true) ?
                                cf.build() : cf.withTrustCerts(true).withDisableHostnameVerification(true).build()
                );
            } catch (Exception e) {
                log.error("Exception during client configuration for cluster {}", clusterUrl, e);
            }
        });
        return configs.stream();
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
