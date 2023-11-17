package com.redhat.quota.extractor.collectors;

import com.redhat.quota.extractor.entities.Nodes;
import io.fabric8.kubernetes.api.model.Node;
import io.fabric8.kubernetes.api.model.NodeStatus;
import io.fabric8.kubernetes.api.model.Quantity;
import io.fabric8.openshift.client.OpenShiftClient;
import jakarta.enterprise.context.ApplicationScoped;
import lombok.extern.slf4j.Slf4j;

import java.util.List;
import java.util.Map;
import java.util.stream.Stream;

@ApplicationScoped
@Slf4j
public class NodesCollector implements ICollector<Nodes> {

    @Override
    public Stream<Nodes> collect(OpenShiftClient openShiftClient, String... namespaces) {
        log.info("collecting nodes for cluster {}", openShiftClient.getMasterUrl());
        List<Node> nodeList = openShiftClient.nodes().list().getItems();
        Stream<Tuple<Map<String, Quantity>, Map<String, Quantity>>> capacitiesAndAllocatables =
                getOcpNodesToCapacityAndAllocatable(nodeList);
        return mapCapacityAndAllocatableToNodes(capacitiesAndAllocatables);
    }

    Stream<Nodes> mapCapacityAndAllocatableToNodes(Stream<Tuple<Map<String, Quantity>, Map<String, Quantity>>> capacityAndAllocatables) {
            /*return capacityAndAllocatables.stream()
                    .map()
                    .toList();*/
        //todo
        return null;
    }

    Stream<Tuple<Map<String, Quantity>, Map<String, Quantity>>> getOcpNodesToCapacityAndAllocatable(List<Node> ocpNodes) {
        return ocpNodes.stream()
                .map(Node::getStatus)
                .map(this::capacityAndAllocatableFromNodeStatus);
    }


    Tuple<Map<String, Quantity>, Map<String, Quantity>> capacityAndAllocatableFromNodeStatus(NodeStatus node) {
        Tuple<Map<String, Quantity>, Map<String, Quantity>> out = new Tuple<>(
                node.getCapacity(),
                node.getAllocatable()
        );
        log.debug("NodeCapacity={}, NodeAllocatable={}", node.getCapacity(), node.getAllocatable());
        return out;
    }

}
