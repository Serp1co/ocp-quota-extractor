package com.redhat.quota.extractor.collectors;

import com.redhat.quota.extractor.entities.Nodes;
import io.fabric8.kubernetes.api.model.Node;
import io.fabric8.kubernetes.api.model.NodeStatus;
import io.fabric8.kubernetes.api.model.Quantity;
import io.fabric8.openshift.client.OpenShiftClient;
import jakarta.enterprise.context.ApplicationScoped;
import lombok.extern.java.Log;
import lombok.extern.slf4j.Slf4j;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@ApplicationScoped
@Slf4j
public class NodesCollector implements ICollector<Nodes> {

    @Override
    public Stream<Nodes> collect(OpenShiftClient openShiftClient, String... namespaces) {
        log.info("Collecting Nodes for cluster %s".formatted(openShiftClient.getMasterUrl()));
        List<Node> nodeList = openShiftClient.nodes().list().getItems();
        log.debug("Nodes={}", nodeList);
        Stream<Tuple<Map<String, Quantity>, Map<String, Quantity>>> capacitiesAndAllocatables =
                getOcpNodesToCapacityAndAllocatable(nodeList);
        log.debug("NodesCapacityAndAllocatables={}", capacitiesAndAllocatables.collect(Collectors.toList()));
        return mapCapacityAndAllocatableToNodes(capacitiesAndAllocatables);
    }

    Stream<Nodes> mapCapacityAndAllocatableToNodes(Stream<Tuple<Map<String, Quantity>, Map<String, Quantity>>> capacityAndAllocatables) {
        return Stream.empty();
    }

    Stream<Tuple<Map<String, Quantity>, Map<String, Quantity>>> getOcpNodesToCapacityAndAllocatable(List<Node> ocpNodes) {
        return ocpNodes.stream()
                .map(Node::getStatus)
                .map(nodeStatus -> new Tuple<>(nodeStatus.getCapacity(), nodeStatus.getAllocatable()));
    }

}
