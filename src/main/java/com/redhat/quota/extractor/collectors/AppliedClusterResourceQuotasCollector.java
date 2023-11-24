package com.redhat.quota.extractor.collectors;

import com.redhat.quota.extractor.entities.AppliedClusterResourceQuotas;
import io.fabric8.openshift.client.OpenShiftClient;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.transaction.Transactional;
import lombok.extern.slf4j.Slf4j;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

@ApplicationScoped
@Slf4j
public class AppliedClusterResourceQuotasCollector extends ACollector implements ICollector<AppliedClusterResourceQuotas> {

    @Override
    public List<AppliedClusterResourceQuotas> collect(OpenShiftClient openShiftClient, String[] namespaces) {
        log.info("collecting AppliedClusterResourceQuotasCollector for cluster {}", openShiftClient.getMasterUrl());
        List<AppliedClusterResourceQuotas> appliedClusterResourceQuotasList = Arrays.stream(namespaces)
                .flatMap(ns -> openShiftClient.quotas()
                        .appliedClusterResourceQuotas()
                        .inNamespace(ns).list().getItems().stream()
                        .map(clusterResourceQuota ->
                                new AppliedClusterResourceQuotas(clusterResourceQuota.getFullResourceName(), ns)
                        )).collect(Collectors.toList());
        persist(appliedClusterResourceQuotasList);
        return appliedClusterResourceQuotasList;
    }

}



