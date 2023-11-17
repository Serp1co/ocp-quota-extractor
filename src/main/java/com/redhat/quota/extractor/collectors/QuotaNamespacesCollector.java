package com.redhat.quota.extractor.collectors;

import com.redhat.quota.extractor.entities.QuotaNamespaces;
import io.fabric8.openshift.client.OpenShiftClient;
import jakarta.enterprise.context.ApplicationScoped;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;

import java.util.Arrays;
import java.util.stream.Stream;

@ApplicationScoped
@Slf4j
public class QuotaNamespacesCollector implements ICollector<QuotaNamespaces> {

    @Override
    public Stream<QuotaNamespaces> collect(OpenShiftClient openShiftClient, String[] namespaces) {
        return Arrays.stream(namespaces)
                .flatMap(ns -> openShiftClient.quotas().clusterResourceQuotas().inNamespace(ns).list().getItems().stream()
                        .map(clusterResourceQuota -> new QuotaNamespaces(clusterResourceQuota.getFullResourceName(), ns))
                );
    }

}



