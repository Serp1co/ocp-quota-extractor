package com.redhat.quota.extractor.collectors;

import com.redhat.quota.extractor.entities.Nodes;
import io.fabric8.kubernetes.api.model.Node;
import io.fabric8.kubernetes.api.model.Quantity;
import io.fabric8.openshift.client.OpenShiftClient;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.transaction.Transactional;
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
        Stream<Tuple<Capacity, Capacity>> capacitiesAllocatable =
                getOcpNodesToCapacityAndAllocatable(nodeList);
        List<Nodes> nodesStream = mapCapacityAllocatableToNodes(capacitiesAllocatable, clusterUrl)
                .collect(Collectors.toList());
        persist(nodesStream);
        return nodesStream;
    }

    Stream<Nodes> mapCapacityAllocatableToNodes(Stream<Tuple<Capacity, Capacity>> capacityAndAllocatables,
                                                String clusterUrl) {
        return capacityAndAllocatables.map(
                tuple -> {
                    Capacity capacity = tuple.getFirst();
                    Capacity allocatable = tuple.getSecond();
                    BigDecimal usedCpu = capacity.cpu.subtract(allocatable.cpu);
                    BigDecimal usedMemory = capacity.memory.subtract(allocatable.memory);
                    BigDecimal usedStorage = capacity.ephemeralStorage.subtract(allocatable.ephemeralStorage);
                    return Nodes.builder()
                            .nodeName("")
                            .cluster(clusterUrl)
                            .CPU(usedCpu)
                            .memory(usedMemory)
                            .disk(usedStorage)
                            .build();
                }
        );

    }

    Stream<Tuple<Capacity, Capacity>> getOcpNodesToCapacityAndAllocatable(List<Node> ocpNodes) {
        return ocpNodes.stream()
                .map(Node::getStatus)
                .map(nodeStatus -> {
                    Map<String, Quantity> capacityMap = nodeStatus.getCapacity();
                    Map<String, Quantity> allocatableMap = nodeStatus.getAllocatable();
                    return new Tuple<>(Capacity.fromMap(capacityMap),
                            Capacity.fromMap(allocatableMap));
                });
    }


    @Builder
    static class Capacity {
        String nodeName;
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
