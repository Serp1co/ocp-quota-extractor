package com.redhat.quota.extractor.collectors;

import com.redhat.quota.extractor.persistance.entities.Namespaces;
import io.fabric8.kubernetes.api.model.Namespace;
import io.fabric8.kubernetes.api.model.ObjectMeta;
import io.fabric8.openshift.client.OpenShiftClient;
import lombok.extern.java.Log;

import java.util.stream.Stream;

@Log
public class NamespacesCollector implements ICollector {

    @Override
    public Stream<Namespaces> collect(OpenShiftClient openShiftClient) {
        log.info("collecting namespaces for cluster=" + openShiftClient.getMasterUrl());
        return getOcpNamespacesToNamespace(openShiftClient);
    }

    static Stream<Namespaces> getOcpNamespacesToNamespace(OpenShiftClient ocpClient) {
        String masterUrl = ocpClient.getMasterUrl().toString();
        return ocpClient.namespaces().list().getItems().stream()
                .map(Namespace::getMetadata)
                .map(ObjectMeta::getName)
                .map(name -> new Namespaces(name, masterUrl));
    }

}
