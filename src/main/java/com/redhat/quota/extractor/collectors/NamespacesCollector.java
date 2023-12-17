package com.redhat.quota.extractor.collectors;

import com.redhat.quota.extractor.entities.Namespaces;
import io.fabric8.kubernetes.api.model.Namespace;
import io.fabric8.kubernetes.api.model.ObjectMeta;
import io.fabric8.openshift.client.OpenShiftClient;
import jakarta.enterprise.context.ApplicationScoped;
import lombok.extern.slf4j.Slf4j;

import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@ApplicationScoped
@Slf4j
public class NamespacesCollector extends ACollector implements ICollector<Namespaces> {

    @Override
    public List<Namespaces> collect(OpenShiftClient openShiftClient, String... namespaces) {
        List<Namespaces> namespacesStream = getOcpNamespacesToNamespace(openShiftClient)
                .collect(Collectors.toList());
        persist(namespacesStream);
        return namespacesStream;
    }

    Stream<Namespaces> getOcpNamespacesToNamespace(OpenShiftClient ocpClient) {
        String masterUrl = ocpClient.getMasterUrl().toString();
        log.info("collecting Namespaces for cluster {}", masterUrl);
        return ocpClient.namespaces().list().getItems().stream()
                .map(Namespace::getMetadata)
                .map(ObjectMeta::getName)
                .map(name -> new Namespaces(name, masterUrl));
    }

}