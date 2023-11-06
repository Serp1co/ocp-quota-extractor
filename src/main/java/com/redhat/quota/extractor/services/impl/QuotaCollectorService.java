package com.redhat.quota.extractor.services.impl;

import com.redhat.quota.extractor.models.Namespaces;
import com.redhat.quota.extractor.models.Nodes;
import com.redhat.quota.extractor.services.interfaces.ICollector;
import com.redhat.quota.extractor.utils.ApiToEntity;
import io.fabric8.kubernetes.api.model.*;
import io.fabric8.openshift.client.OpenShiftClient;
import jakarta.enterprise.context.RequestScoped;
import jakarta.inject.Inject;
import lombok.Getter;

import java.util.List;
import java.util.Map;

@RequestScoped
public class QuotaCollectorService implements ICollector {

    @Inject
    OpenShiftClient openshiftClient;

    @Override
    public List<Namespaces> collectNamespaces() {
        return openshiftClient.namespaces()
                .list()
                .getItems()
                .stream()
                .map(Namespace::getMetadata)
                .map(ObjectMeta::getName)
                .map(ApiToEntity::NAMESPACES)
                .toList()
                //.forEach(ns -> ns.persist())
                ;
    }

    @Override
    public List<Nodes> collectNodes() {
        List<Node> nodeList = openshiftClient.nodes().list().getItems();
        List<NodeCapacityAndAllocatable> nodeCapacityAndAllocatableList = nodeList.stream()
                .map(Node::getStatus)
                .map(NodeCapacityAndAllocatable::fromK8sNodeStatus)
                .toList();
        //todo
        return null;
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
