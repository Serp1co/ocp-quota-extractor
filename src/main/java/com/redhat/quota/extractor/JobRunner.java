package com.redhat.quota.extractor;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.redhat.quota.extractor.exception.ApplicationException;
import com.redhat.quota.extractor.models.Namespaces;
import com.redhat.quota.extractor.utils.ApiToEntity;
import io.fabric8.kubernetes.api.model.*;
import io.fabric8.kubernetes.client.Config;
import io.fabric8.kubernetes.client.ConfigBuilder;
import io.fabric8.kubernetes.client.KubernetesClient;
import io.fabric8.kubernetes.client.KubernetesClientBuilder;
import io.fabric8.openshift.client.OpenShiftClient;
import io.quarkus.scheduler.Scheduled;
import io.smallrye.config.ConfigMapping;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.transaction.Transactional;
import lombok.Getter;
import lombok.extern.java.Log;
import com.redhat.quota.extractor.services.OCPLoginService;
import org.eclipse.microprofile.config.inject.ConfigProperty;

import java.util.List;
import java.util.Map;
import java.util.Optional;

@ApplicationScoped
@Log
public class JobRunner {

    @Inject
    ExtractorConfigs extractorConfigs;
    @Inject
    OCPLoginService OCPLoginService;

    @ConfigProperty(name = "extractor.login.use-sa-auth", defaultValue = "true")
    boolean useSaAuth;

    static void builderAddOcpClientBasicAuthConfig(ConfigBuilder cf, final String cluster, final String oauthToken) {
        cf.withDisableHostnameVerification(true).withTrustCerts(true).withOauthToken(oauthToken);
    }

    static void builderAddOcpClientSaAuthConfig(ConfigBuilder cf, final String cluster) {
        cf.withDisableHostnameVerification(true).withTrustCerts(true);
    }

    public static List<Namespaces> getNamespacesFromApi(OpenShiftClient ocp_client, String cluster) {
        return ocp_client.namespaces()
                .list()
                .getItems()
                .stream()
                .map(Namespace::getMetadata)
                .map(ObjectMeta::getName)
                .map(ns_name -> ApiToEntity.NAMESPACES(ns_name, cluster))
                .toList()
                //.forEach(ns -> ns.persist())
                ;
    }

    @Transactional
    @Scheduled(cron = "${job.schedule.time: 0 0 10 * * ?}")
    void schedule() throws ApplicationException, JsonProcessingException {
        doJob();
    }

    public void doJob() throws ApplicationException, JsonProcessingException {
        Optional<List<?>> out = Optional.empty();
        for (String cluster : extractorConfigs.clustersUrl()) {
            ConfigBuilder ocpConfigBuilder = new ConfigBuilder().withMasterUrl(cluster);
            if(useSaAuth) {
                builderAddOcpClientSaAuthConfig(ocpConfigBuilder, cluster);
            }
            else {
                String token = OCPLoginService.login();
                builderAddOcpClientBasicAuthConfig(ocpConfigBuilder, cluster, token);
            }
            try (KubernetesClient generic_kube_client = new KubernetesClientBuilder().withConfig(ocpConfigBuilder.build()).build()) {
                try (OpenShiftClient ocp_client = generic_kube_client.adapt(OpenShiftClient.class)) {
                    List<Namespaces> namespacesList = getNamespacesFromApi(ocp_client, cluster);
                    List<Node> nodeList = ocp_client.nodes().list().getItems();
                    List<NodeCapacityAndAllocatable> nodeCapacityAndAllocatableList = nodeList.stream()
                            .map(Node::getStatus)
                            .map(NodeCapacityAndAllocatable::fromK8sNodeStatus)
                            .toList();
                }
            }
        }
    }

    @ConfigMapping(prefix = "extractor")
    interface ExtractorConfigs {

        List<String> clustersUrl();

    }

    @Getter
    public static class NodeCapacityAndAllocatable {
        Map<String, Quantity> capacity;
        Map<String, Quantity> allocatable;

        NodeCapacityAndAllocatable() {}

        public static NodeCapacityAndAllocatable fromK8sNodeStatus(NodeStatus node) {
            NodeCapacityAndAllocatable out = new NodeCapacityAndAllocatable();
            out.capacity = node.getCapacity();
            out.allocatable = node.getAllocatable();
            return out;
        }

    }

}

