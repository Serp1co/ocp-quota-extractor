package com.redhat.quota.extractor.services;

import com.redhat.quota.extractor.collectors.ClusterResourceQuotasCollector;
import com.redhat.quota.extractor.collectors.NamespacesCollector;
import com.redhat.quota.extractor.collectors.NodesCollector;
import com.redhat.quota.extractor.collectors.AppliedQuotaForNamespacesCollector;
import com.redhat.quota.extractor.entities.Namespaces;
import com.redhat.quota.extractor.entities.commons.ExtractorEntity;
import io.fabric8.kubernetes.client.ConfigBuilder;
import io.fabric8.kubernetes.client.KubernetesClientBuilder;
import io.fabric8.openshift.client.OpenShiftClient;
import jakarta.enterprise.context.RequestScoped;
import jakarta.inject.Inject;
import lombok.extern.slf4j.Slf4j;
import org.eclipse.microprofile.config.inject.ConfigProperty;

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
    AppliedQuotaForNamespacesCollector appliedQuotaForNamespacesCollector;

    @Inject
    @ConfigProperty(name = "extractor.client.clusters-url")
    Set<String> clusters;


    public void executeExtraction() {
        clusters.stream().parallel().forEach(clusterUrl -> {
                    try (
                            OpenShiftClient client = new KubernetesClientBuilder()
                                    .withConfig(
                                            new ConfigBuilder()
                                                    .withAutoConfigure(false)
                                                    .withUsername("kubeadmin")
                                                    .withPassword("LrEb6-fpbzR-92jT7-WPv2f")
                                                    .withTrustCerts(true)
                                                    .withDisableHostnameVerification(true)
                                                    .withMasterUrl(clusterUrl)
                                                    .build()
                                    )
                                    .build()
                                    .adapt(OpenShiftClient.class)
                    ) {
                        String[] namespaces = namespacesCollector.collect(client).parallel()
                                .peek(ExtractorEntity::persistEntityBlocking)
                                .map(Namespaces::getNamespaceName)
                                .toArray(String[]::new);
                        nodesCollector.collect(client).parallel()
                                .forEach(ExtractorEntity::persistEntityBlocking);
                        clusterResourceQuotasCollector.collect(client).parallel()
                                .forEach(ExtractorEntity::persistEntityBlocking);
                        appliedQuotaForNamespacesCollector.collect(client, namespaces).parallel()
                                .forEach(ExtractorEntity::persistEntityBlocking);
                    } catch (Exception ex) {
                        log.error("Exception during extraction for cluster {}", clusterUrl, ex);
                    }
                }
        );
    }

}
