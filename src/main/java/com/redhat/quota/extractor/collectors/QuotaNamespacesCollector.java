package com.redhat.quota.extractor.collectors;

import com.redhat.quota.extractor.entities.AppliedQuotaForNamespaces;
import io.fabric8.openshift.client.OpenShiftClient;
import jakarta.enterprise.context.ApplicationScoped;
import lombok.extern.slf4j.Slf4j;

import java.util.Arrays;
import java.util.stream.Stream;

@ApplicationScoped
@Slf4j
public class QuotaNamespacesCollector implements ICollector<AppliedQuotaForNamespaces> {

    @Override
    public Stream<AppliedQuotaForNamespaces> collect(OpenShiftClient openShiftClient, String[] namespaces) {
        log.info("collecting AppliedQuotaForNamespaces for cluster {}", openShiftClient.getMasterUrl());
        return Arrays.stream(namespaces)
                .flatMap(ns -> openShiftClient.quotas().clusterResourceQuotas().inNamespace(ns).list().getItems().stream()
                        .map(clusterResourceQuota -> new AppliedQuotaForNamespaces(clusterResourceQuota.getFullResourceName(), ns))
                );
    }

}



