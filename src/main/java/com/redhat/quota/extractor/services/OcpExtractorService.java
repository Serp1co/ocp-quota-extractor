package com.redhat.quota.extractor.services;

import com.redhat.quota.extractor.persistance.entities.ExtractorEntity;
import com.redhat.quota.extractor.persistance.entities.Namespaces;
import com.redhat.quota.extractor.persistance.entities.Nodes;
import com.redhat.quota.extractor.providers.OcpClientConfig;
import io.fabric8.kubernetes.api.model.*;
import io.fabric8.kubernetes.client.ConfigBuilder;
import io.fabric8.kubernetes.client.KubernetesClientBuilder;
import io.fabric8.openshift.client.OpenShiftClient;
import jakarta.annotation.PostConstruct;
import jakarta.annotation.PreDestroy;
import jakarta.enterprise.context.RequestScoped;
import jakarta.inject.Inject;
import lombok.Getter;
import lombok.extern.java.Log;
import org.eclipse.microprofile.config.inject.ConfigProperty;

import java.util.List;
import java.util.Map;
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
     * execute the collection
     */
    public void executeFullExtraction() {
        Stream<Function<OpenShiftClient, Stream<? extends ExtractorEntity>>> collectionOperations =
                Stream.of(
                        OcpCollectionOperation::collectNamespaces,
                        OcpCollectionOperation::collectNodes
                );
        clusters.forEach(
                clusterUrl ->
                        executeExtractions(
                                clusterUrl,
                                collectionOperations.parallel(),
                                ExtractorEntity::persistEntities
                        )
        );
    }

    /**
     * builds the client with a try/with resource operation, for each client applies all the collection operations
     * then persist the result of the collection on the database
     *
     * @param clusterUrl           the cluster to perform the collection against
     * @param collectionOperations the collection operations stream to be performed
     * @param persistOperation     the persist operation to be performed on the cluster operations result
     */
    public void executeExtractions(String clusterUrl,
                                   Stream<Function<OpenShiftClient, Stream<? extends ExtractorEntity>>> collectionOperations,
                                   Function<Stream<? extends ExtractorEntity>, Void> persistOperation) {
        try (
                OpenShiftClient client = new KubernetesClientBuilder()
                        .withConfig(new ConfigBuilder(ocpClientConfig.getConfig()).withMasterUrl(clusterUrl).build())
                        .build()
                        .adapt(OpenShiftClient.class)
        ) {
            collectionOperations.forEach(operation -> operation.andThen(persistOperation).apply(client));
        }
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
            collectionOperation.andThen(persistOperation).apply(client);
        }
    }

    /**
     * execute a collection without persisting anything
     *
     * @param client              the client to perform the collection against
     * @param collectionOperation the collection operation to be performed
     * @return the results of the collection performed
     */
    public Stream<? extends ExtractorEntity> executeCollection(OpenShiftClient client,
                                                               Function<OpenShiftClient, Stream<? extends ExtractorEntity>> collectionOperation) {
        log.fine("applying collection operation for cluster=" + client.getMasterUrl());
        return collectionOperation.apply(client);
    }


    static class OcpCollectionOperation {

        static Stream<Namespaces> collectNamespaces(OpenShiftClient openShiftClient) {
            log.info("collecting namespaces for cluster=" + openShiftClient.getMasterUrl());
            return ExtractorCollectionUtil.getOcpNamespacesToNamespace(openShiftClient);
        }

        static Stream<Nodes> collectNodes(OpenShiftClient openShiftClient) {
            log.info("collecting nodes for cluster=" + openShiftClient.getMasterUrl());
            List<Node> nodeList = openShiftClient.nodes().list().getItems();
            Stream<ExtractorCollectionUtil.NodeCapacityAndAllocatable> capacitiesAndAllocatables =
                    ExtractorCollectionUtil.getOcpNodesToCapacityAndAllocatable(nodeList);
            //todo
            return null;
        }

        static class ExtractorCollectionUtil {

            public static List<Nodes> mapCapacityAndAllocatableToNodes
                    (List<NodeCapacityAndAllocatable> capacityAndAllocatables) {
            /*return capacityAndAllocatables.stream()
                    .map()
                    .toList();*/
                //todo
                return null;
            }

            static Stream<NodeCapacityAndAllocatable> getOcpNodesToCapacityAndAllocatable(List<Node> ocpNodes) {
                return ocpNodes.stream()
                        .map(Node::getStatus)
                        .map(NodeCapacityAndAllocatable::fromK8sNodeStatus);
            }

            static Stream<Namespaces> getOcpNamespacesToNamespace(OpenShiftClient ocpClient) {
                String masterUrl = ocpClient.getMasterUrl().toString();
                return ocpClient.namespaces().list().getItems().stream()
                        .map(Namespace::getMetadata)
                        .map(ObjectMeta::getName)
                        .map(name -> new Namespaces(name, masterUrl));
            }

            @Getter
            public static class NodeCapacityAndAllocatable {
                Map<String, Quantity> capacity;
                Map<String, Quantity> allocatable;

                NodeCapacityAndAllocatable() {
                }

                public static NodeCapacityAndAllocatable fromK8sNodeStatus(NodeStatus node) {
                    NodeCapacityAndAllocatable out = new NodeCapacityAndAllocatable();
                    out.capacity = node.getCapacity();
                    out.allocatable = node.getAllocatable();
                    return out;
                }

            }

        }

    }

}
