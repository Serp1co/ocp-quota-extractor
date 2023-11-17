package com.redhat.quota.extractor.collectors;

import com.redhat.quota.extractor.entities.ClusterResourceQuotas;
import io.fabric8.kubernetes.api.model.Quantity;
import io.fabric8.openshift.api.model.ClusterResourceQuota;
import io.fabric8.openshift.api.model.ClusterResourceQuotaStatus;
import io.fabric8.openshift.client.OpenShiftClient;
import jakarta.enterprise.context.ApplicationScoped;
import lombok.extern.slf4j.Slf4j;

import java.util.List;
import java.util.Map;
import java.util.stream.Stream;

@ApplicationScoped
@Slf4j
public class ClusterResourceQuotasCollector implements ICollector<ClusterResourceQuotas> {

    @Override
    public Stream<ClusterResourceQuotas> collect(OpenShiftClient openShiftClient, String... namespaces) {
        log.info("collecting namespaces for cluster {}", openShiftClient.getMasterUrl());
        return getOcpClusterResourceQuotaToClusterResourceQuotas(openShiftClient);
    }

    Stream<ClusterResourceQuotas> getOcpClusterResourceQuotaToClusterResourceQuotas(OpenShiftClient ocpClient) {
        String masterUrl = ocpClient.getMasterUrl().toString();
        List<ClusterResourceQuota> clusterResourceQuotaList =
                ocpClient.quotas().clusterResourceQuotas().list().getItems();
        return clusterResourceQuotaList.stream().parallel()
                .map(cls -> new Tuple<>(cls.getFullResourceName(), cls.getStatus()))
                .map(tuple -> {
                    ClusterResourceQuotaStatus status = tuple.getSecond();
                    String quotaName = tuple.getFirst();
                    log.debug("ClusterResourceQuotaStatus={}, QuotaName={}", status, quotaName);
                    Map<String, Quantity> hard = status.getTotal().getHard();
                    Map<String, Quantity> used = status.getTotal().getUsed();
                    return ClusterResourceQuotas.builder()
                            .Cluster(masterUrl)
                            .QuotaName(quotaName)
                            /*.LimitsCPU()
                            .UsedLimitCPU()
                            .UsedRequestCPU()
                            .RequestMemory()
                            .UsedRequestMemory()
                            //.ServiceModel()
                            //.RequestID()
                            //.Ambito()
                            //.Application()
                            */.build();
                });
    }


}
