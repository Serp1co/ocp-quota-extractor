package com.redhat.quota.extractor.services;

import com.redhat.quota.extractor.collectors.*;
import com.redhat.quota.extractor.entities.Namespaces;
import io.fabric8.kubernetes.client.Config;
import io.fabric8.kubernetes.client.ConfigBuilder;
import io.fabric8.kubernetes.client.KubernetesClientBuilder;
import io.fabric8.openshift.client.OpenShiftClient;
import io.smallrye.config.ConfigMapping;
import jakarta.enterprise.context.RequestScoped;
import jakarta.inject.Inject;
import lombok.extern.slf4j.Slf4j;

import java.util.Optional;
import java.util.Set;

@RequestScoped
@Slf4j
public class OcpExtractorService {

    @Inject
    NamespacesCollector namespacesCollector;
    @Inject
    NodesCollector nodesCollector;
    @Inject
    ClusterResourceQuotasCollector clusterResourceQuotasCollector;
    @Inject
    AppliedClusterResourceQuotasCollector appliedClusterResourceQuotasCollector;
    @Inject
    LabelsCollector labelsCollector;
    @Inject
    AnnotationsCollector annotationsCollector;
    @Inject
    ExtractorClientConfig extractorClientConfig;

    static Config getConfig(String clusterUrl, ExtractorClientConfig extractorClientConfig) {
        ConfigBuilder cf = new ConfigBuilder()
                .withAutoConfigure(false)
                .withMasterUrl(clusterUrl)
                .withUsername(extractorClientConfig.username())
                .withPassword(extractorClientConfig.password());
        return extractorClientConfig.ssl().orElse(true) ?
                cf.build() : cf.withTrustCerts(true).withDisableHostnameVerification(true).build();
    }

    public void executeExtraction() {
        extractorClientConfig.clusters().stream().parallel().forEach(clusterUrl -> {
                    try (
                            OpenShiftClient client = new KubernetesClientBuilder()
                                    .withConfig(
                                            getConfig(clusterUrl, extractorClientConfig)
                                    )
                                    .build()
                                    .adapt(OpenShiftClient.class)
                    ) {
                        String[] namespaces = namespacesCollector.collect(client)
                                .stream().parallel()
                                .map(Namespaces::getNamespaceName)
                                .toArray(String[]::new);
                        nodesCollector.collect(client);
                        clusterResourceQuotasCollector.collect(client);
                        appliedClusterResourceQuotasCollector.collect(client, namespaces);
                    } catch (Exception ex) {
                        log.error("Exception during extraction for cluster {}", clusterUrl, ex);
                    }
                }
        );
    }

    @ConfigMapping(prefix = "extractor.client")
    interface ExtractorClientConfig {
        Set<String> clusters();
        String username();
        String password();
        Optional<Boolean> ssl();
    }

}
