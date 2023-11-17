package com.redhat.quota.extractor.collectors;

import com.redhat.quota.extractor.entities.Namespaces;
import io.fabric8.kubernetes.api.model.Namespace;
import io.fabric8.kubernetes.api.model.ObjectMeta;
import io.fabric8.openshift.client.OpenShiftClient;
import jakarta.enterprise.context.ApplicationScoped;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;

import java.util.stream.Stream;

@ApplicationScoped
@Slf4j
public class NamespacesCollector implements ICollector<Namespaces> {

    @Override
    public Stream<Namespaces> collect(OpenShiftClient openShiftClient, String... namespaces) {
        log.info("collecting namespaces for cluster {}", openShiftClient.getMasterUrl());
        return getOcpNamespacesToNamespace(openShiftClient);
    }

    Stream<Namespaces> getOcpNamespacesToNamespace(OpenShiftClient ocpClient) {
        String masterUrl = ocpClient.getMasterUrl().toString();
        return ocpClient.namespaces().list().getItems().stream()
                .map(Namespace::getMetadata)
                .map(ObjectMeta::getName)
                .map(name -> new Namespaces(name, masterUrl));
    }

}

