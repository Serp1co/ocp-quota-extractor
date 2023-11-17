package com.redhat.quota.extractor.collectors;

import com.redhat.quota.extractor.entities.Nodes;
import io.fabric8.kubernetes.api.model.Node;
import io.fabric8.kubernetes.api.model.NodeStatus;
import io.fabric8.kubernetes.api.model.Quantity;
import io.fabric8.openshift.client.OpenShiftClient;
import jakarta.enterprise.context.ApplicationScoped;
import lombok.Getter;
import lombok.extern.java.Log;
import lombok.extern.slf4j.Slf4j;

import java.util.List;
import java.util.Map;
import java.util.stream.Stream;

@Getter
@ApplicationScoped
@Slf4j
public class NodesCollector implements ICollector<Nodes> {

    public Stream<Nodes> collect(OpenShiftClient openShiftClient, String... namespaces) {
        log.info("collecting nodes for cluster {}", openShiftClient.getMasterUrl());
        List<Node> nodeList = openShiftClient.nodes().list().getItems();
        Stream<NodeCapacityAndAllocatable> capacitiesAndAllocatables = getOcpNodesToCapacityAndAllocatable(nodeList);
        return null;
    }

    static List<Nodes> mapCapacityAndAllocatableToNodes(List<NodeCapacityAndAllocatable> capacityAndAllocatables) {
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


    @Getter
    static class NodeCapacityAndAllocatable {
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
