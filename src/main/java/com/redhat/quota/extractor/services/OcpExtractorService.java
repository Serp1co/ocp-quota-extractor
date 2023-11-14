package com.redhat.quota.extractor.services;

import com.redhat.quota.extractor.persistance.entities.commons.ExtractorEntity;
import com.redhat.quota.extractor.providers.OcpClientConfig;
import com.redhat.quota.extractor.collectors.ICollector;
import io.fabric8.kubernetes.client.ConfigBuilder;
import io.fabric8.kubernetes.client.KubernetesClientBuilder;
import io.fabric8.openshift.client.OpenShiftClient;
import jakarta.annotation.PostConstruct;
import jakarta.annotation.PreDestroy;
import jakarta.enterprise.context.RequestScoped;
import jakarta.inject.Inject;
import lombok.extern.java.Log;
import org.eclipse.microprofile.config.inject.ConfigProperty;

import java.util.Collection;
import java.util.Set;
import java.util.function.Function;
import java.util.stream.Stream;

@RequestScoped
@Log
public class OcpExtractorService {

    @Inject
    OcpClientConfig ocpClientConfig;

    @Inject
    @ConfigProperty(name = "extractor.client.clusters-url")
    Set<String> clusters;

    /**
     * do nothing (for now)
     */
    @PostConstruct
    void construct() {
    }

    /**
     * do nothing (for now)
     */
    @PreDestroy
    void destroy() {
    }

    /**
     * Execute a standard extraction on the clusters using the provided collectors
     * @param collectors some ICollector
     */
    public void executeExtraction(Collection<ICollector> collectors) {
        clusters.stream().parallel().forEach(clusterUrl ->
                collectors.stream().parallel().forEach(collector ->
                        executeExtraction(clusterUrl, collector::collect, ExtractorEntity::persistEntities)
                )
        );
    }

    /**
     * builds the client with a try/with resource operation, for each client applies all the collection operations
     * then persist the result of the collection on the database
     *
     * @param clusterUrl          the cluster to perform the collection against
     * @param collectionOperation the collection operation to be performed
     * @param persistOperation    the persist operation to be performed on the cluster operation result
     */
    public void executeExtraction(String clusterUrl,
                                  Function<OpenShiftClient, Stream<? extends ExtractorEntity>> collectionOperation,
                                  Function<Stream<? extends ExtractorEntity>, Void> persistOperation) {
        try (
                OpenShiftClient client = new KubernetesClientBuilder()
                        .withConfig(new ConfigBuilder(ocpClientConfig.getConfig()).withMasterUrl(clusterUrl).build())
                        .build()
                        .adapt(OpenShiftClient.class)
        ) {
            log.fine("applying extraction operation for cluster=" + clusterUrl);
            collectionOperation.andThen(persistOperation).apply(client);
        }
    }

}
