package com.redhat.quota.extractor.services;

import com.redhat.quota.extractor.collectors.*;
import com.redhat.quota.extractor.entities.Namespaces;
import com.redhat.quota.extractor.entities.Nodes;
import com.redhat.quota.extractor.entities.QuotaNamespaces;
import com.redhat.quota.extractor.entities.commons.ExtractorEntity;
import com.redhat.quota.extractor.providers.OcpClientConfig;
import io.fabric8.kubernetes.client.ConfigBuilder;
import io.fabric8.kubernetes.client.KubernetesClientBuilder;
import io.fabric8.openshift.client.OpenShiftClient;
import io.smallrye.common.annotation.Blocking;
import jakarta.enterprise.context.RequestScoped;
import jakarta.inject.Inject;
import jakarta.inject.Named;
import lombok.extern.java.Log;
import lombok.extern.slf4j.Slf4j;
import org.eclipse.microprofile.config.inject.ConfigProperty;

import java.util.Collection;
import java.util.List;
import java.util.Set;
import java.util.function.BiFunction;
import java.util.function.Function;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@RequestScoped
@Slf4j
public class OcpExtractorService {

    @Inject
    OcpClientConfig ocpClientConfig;

    @Inject
    NamespacesCollector namespacesCollector;

    @Inject
    NodesCollector nodesCollector;

    @Inject
    ClusterResourceQuotasCollector clusterResourceQuotasCollector;

    @Inject
    QuotaNamespacesCollector quotaNamespacesCollector;

    @Inject
    @ConfigProperty(name = "extractor.client.clusters-url")
    Set<String> clusters;


    public void executeExtraction() {
        clusters.stream().parallel().forEach(clusterUrl -> {
                    try (
                            OpenShiftClient client = new KubernetesClientBuilder()
                                    .withConfig(new ConfigBuilder(ocpClientConfig.getConfig()).withMasterUrl(clusterUrl).build())
                                    .build()
                                    .adapt(OpenShiftClient.class)
                    ) {
                        nodesCollector.collect(client).parallel().forEach(this::persist);
                        clusterResourceQuotasCollector.collect(client).parallel().forEach(this::persist);
                        String[] namespaces = namespacesCollector.collect(client).parallel()
                                .peek(this::persist)
                                .map(Namespaces::getNamespaceName)
                                .toArray(String[]::new);
                        quotaNamespacesCollector.collect(client, namespaces).parallel().forEach(this::persist);
                    } catch (Exception ex) {
                        log.error("Exception during extraction for cluster {}", clusterUrl, ex);
                    }
                }
        );
    }

    @Blocking
    void persist(ExtractorEntity entity) {
        entity.persist();
    }

}
