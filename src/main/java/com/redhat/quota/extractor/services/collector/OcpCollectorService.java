package com.redhat.quota.extractor.services.collector;

import com.redhat.quota.extractor.persistance.models.Namespaces;
import com.redhat.quota.extractor.persistance.models.Nodes;
import com.redhat.quota.extractor.utils.ApiToEntity;
import io.fabric8.kubernetes.api.model.*;
import io.fabric8.openshift.client.OpenShiftClient;
import jakarta.enterprise.context.RequestScoped;
import jakarta.inject.Inject;
import lombok.Getter;
import lombok.extern.java.Log;

import java.util.List;
import java.util.Map;

@RequestScoped
@Log
public class OcpCollectorService {

    @Inject
    Map<String, OpenShiftClient> ocpClusterClientMap;

    public void doFullCollection() {
        ocpClusterClientMap.forEach((cluster, client) ->
                collectNamespaces(cluster, client).forEach(
                        ns -> log.info(ns.toString())
                ));
    }

    List<Namespaces> collectNamespaces(String cluster, OpenShiftClient openShiftClient) {
        return ExtractorCollectionUtil.getOcpNamespacesToNamespace(
                openShiftClient,
                cluster);
    }

    List<Nodes> collectNodes(String cluster, OpenShiftClient openShiftClient) {
        List<Node> nodeList = openShiftClient.nodes().list().getItems();
        List<ExtractorCollectionUtil.NodeCapacityAndAllocatable> capAndAll =
                ExtractorCollectionUtil.getOcpNodesToCapacityAndAllocatable(nodeList);
        //todo
        return null;
    }


    static class ExtractorCollectionUtil {

        public static List<Nodes> mapCapacityAndAllocatableToNodes
                (List<NodeCapacityAndAllocatable>capacityAndAllocatables)
        {
            /*return capacityAndAllocatables.stream()
                    .map()
                    .toList();*/
            //todo
            return null;
        }

        static List<NodeCapacityAndAllocatable> getOcpNodesToCapacityAndAllocatable(List<Node> ocpNodes) {
            return ocpNodes.stream()
                    .map(Node::getStatus)
                    .map(NodeCapacityAndAllocatable::fromK8sNodeStatus)
                    .toList();
        }

        static List<Namespaces> getOcpNamespacesToNamespace(OpenShiftClient ocpClient, String cluster) {
            return ocpClient.namespaces().list().getItems().stream()
                    .map(Namespace::getMetadata)
                    .map(ObjectMeta::getName)
                    .map(name -> ApiToEntity.NAMESPACES(name, cluster))
                    .toList();
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
