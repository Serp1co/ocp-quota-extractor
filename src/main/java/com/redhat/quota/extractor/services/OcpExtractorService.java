package com.redhat.quota.extractor.services;

import com.redhat.quota.extractor.collectors.*;
import com.redhat.quota.extractor.entities.Namespaces;
import io.fabric8.kubernetes.client.KubernetesClientBuilder;
import io.fabric8.openshift.client.OpenShiftClient;
import jakarta.enterprise.context.RequestScoped;
import jakarta.inject.Inject;
import lombok.extern.slf4j.Slf4j;

@RequestScoped
@Slf4j
public class OcpExtractorService {

    @Inject
    ClientConfigService clientConfigService;
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


    public void executeExtraction() {
        clientConfigService.getConfigsForClusters().forEach(config -> {
            try (
                            OpenShiftClient client = new KubernetesClientBuilder()
                                    .withConfig(config)
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
                        log.error("Exception during extraction for cluster {}", config.getMasterUrl(), ex);
                    }
                }
        );
    }


}
