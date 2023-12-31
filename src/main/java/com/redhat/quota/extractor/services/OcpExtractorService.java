package com.redhat.quota.extractor.services;

import com.redhat.quota.extractor.collectors.*;
import com.redhat.quota.extractor.entities.Namespaces;
import io.fabric8.kubernetes.client.Config;
import io.fabric8.kubernetes.client.KubernetesClientBuilder;
import io.fabric8.openshift.client.OpenShiftClient;
import io.smallrye.common.annotation.Blocking;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import lombok.extern.slf4j.Slf4j;

@ApplicationScoped
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

    @Blocking
    public void executeExtraction(Config config) {
        log.info("START - full collection job for cluster={}", config.getMasterUrl());
        try (OpenShiftClient client = new KubernetesClientBuilder()
                .withConfig(config)
                .build()
                .adapt(OpenShiftClient.class)) {
            String[] namespaces = namespacesCollector.collect(client).stream().parallel()
                    .map(Namespaces::getNamespaceName)
                    .toArray(String[]::new);
            nodesCollector.collect(client);
            clusterResourceQuotasCollector.collect(client);
            appliedClusterResourceQuotasCollector.collect(client, namespaces);
        } catch (Exception ex) {
            log.error("Exception during extraction for cluster={}", config.getMasterUrl(), ex);
        }
        log.info("END - full collection job end for cluster={}", config.getMasterUrl());
    }

}
