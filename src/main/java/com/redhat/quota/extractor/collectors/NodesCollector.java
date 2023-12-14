package com.redhat.quota.extractor.collectors;

import com.redhat.quota.extractor.entities.Nodes;
import io.fabric8.kubernetes.api.model.Node;
import io.fabric8.kubernetes.api.model.NodeStatus;
import io.fabric8.kubernetes.api.model.Quantity;
import io.fabric8.openshift.client.OpenShiftClient;
import jakarta.enterprise.context.ApplicationScoped;
import lombok.Builder;
import lombok.extern.slf4j.Slf4j;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@ApplicationScoped
@Slf4j
public class NodesCollector extends ACollector implements ICollector<Nodes> {

    @Override
    public List<Nodes> collect(OpenShiftClient openShiftClient, String... namespaces) {
        String clusterUrl = openShiftClient.getMasterUrl().toString();
        log.info("Collecting Nodes for cluster={}", clusterUrl);
        List<Node> nodeList = openShiftClient.nodes().list().getItems();
        log.debug("nodeList={}", nodeList);
        Stream<Tuple<Tuple<Capacity, Capacity>, String>> nodesInfo =
                getOcpNodesInfo(nodeList);
        List<Nodes> nodesStream = mapNodesInfo(nodesInfo, clusterUrl)
                .collect(Collectors.toList());
        persist(nodesStream);
        return nodesStream;
    }

    Stream<Nodes> mapNodesInfo(Stream<Tuple<Tuple<Capacity, Capacity>, String>> nodesInfo,
                                                String clusterUrl) {
        return nodesInfo.map(
                tuple -> {
                    Capacity capacity = tuple.getFirst().getFirst();
                    Capacity allocatable = tuple.getFirst().getSecond();
                    String nodeName = tuple.getSecond();
                    return Nodes.builder()
                            .nodeName(nodeName)
                            .cluster(clusterUrl)
                            .allocatableCPU(allocatable.cpu)
                            .allocatableMemory(allocatable.memory)
                            .allocatableDisk(allocatable.ephemeralStorage)
                            .capacityCPU(capacity.cpu)
                            .capacityMemory(capacity.memory)
                            .capacityDisk(capacity.ephemeralStorage)
                            .build();
                }
        );

    }

    Stream<Tuple<Tuple<Capacity, Capacity>, String>> getOcpNodesInfo(List<Node> ocpNodes) {
        return ocpNodes.stream()
                .map(node -> new Tuple<>(node.getMetadata().getName(), node.getStatus()))
                .map(nodeTuple -> {
                    String nodeName = nodeTuple.getFirst();
                    NodeStatus nodeStatus = nodeTuple.getSecond();
                    Map<String, Quantity> capacityMap = nodeStatus.getCapacity();
                    Map<String, Quantity> allocatableMap = nodeStatus.getAllocatable();
                    return new Tuple<>(new Tuple<>(Capacity.fromMap(capacityMap),
                            Capacity.fromMap(allocatableMap)), nodeName);
                });
    }


    @Builder
    static class Capacity {
        BigDecimal cpu;
        BigDecimal ephemeralStorage;
        BigDecimal memory;
        BigDecimal pods;

        static Capacity fromMap(Map<String, Quantity> map) {
            return Capacity.builder()
                    .cpu(map.get("cpu").getNumericalAmount())
                    .pods(map.get("pods").getNumericalAmount())
                    .ephemeralStorage(map.get("ephemeral-storage").getNumericalAmount())
                    .memory(map.get("memory").getNumericalAmount())
                    .build();
        }
    }

}
