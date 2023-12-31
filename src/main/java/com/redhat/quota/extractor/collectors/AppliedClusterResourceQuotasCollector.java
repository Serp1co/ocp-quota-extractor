package com.redhat.quota.extractor.collectors;

import com.redhat.quota.extractor.entities.AppliedClusterResourceQuotas;
import io.fabric8.openshift.client.OpenShiftClient;
import jakarta.enterprise.context.ApplicationScoped;
import lombok.extern.slf4j.Slf4j;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@ApplicationScoped
@Slf4j
public class AppliedClusterResourceQuotasCollector extends ACollector
        implements ICollector<AppliedClusterResourceQuotas> {

    @Override
    public List<AppliedClusterResourceQuotas> collect(OpenShiftClient openShiftClient, String[] namespaces) {
        String masterUrl = openShiftClient.getMasterUrl().toString();
        log.info("START - collecting AppliedClusterResourceQuotasCollector for cluster={}", masterUrl);
        List<AppliedClusterResourceQuotas> appliedClusterResourceQuotasList = Arrays.stream(namespaces)
                .flatMap(ns -> openShiftClient.quotas()
                        .appliedClusterResourceQuotas()
                        .inNamespace(ns).list().getItems().stream()
                        .map(appliedClusterResourceQuota -> AppliedClusterResourceQuotas.builder()
                                .clusterResourceQuotaName(appliedClusterResourceQuota.getMetadata().getName())
                                .cluster(masterUrl)
                                .namespace(ns)
                                .build()))
                .collect(Collectors.toList());
        persist(appliedClusterResourceQuotasList);
        log.info("END - collecting AppliedClusterResourceQuotasCollector for cluster={}", masterUrl);
        return appliedClusterResourceQuotasList;
    }

}
