package com.redhat.quota.extractor.collectors;

import com.redhat.quota.extractor.entities.ClusterResourceQuotas;
import io.fabric8.kubernetes.api.model.Quantity;
import io.fabric8.openshift.api.model.ClusterResourceQuota;
import io.fabric8.openshift.api.model.ClusterResourceQuotaSpec;
import io.fabric8.openshift.api.model.ClusterResourceQuotaStatus;
import io.fabric8.openshift.client.OpenShiftClient;
import jakarta.enterprise.context.ApplicationScoped;
import lombok.extern.slf4j.Slf4j;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@ApplicationScoped
@Slf4j
public class ClusterResourceQuotasCollector implements ICollector<ClusterResourceQuotas> {

    @Override
    public Stream<ClusterResourceQuotas> collect(OpenShiftClient openShiftClient, String... namespaces) {
        log.info("collecting ClusterResourceQuotas for cluster {}", openShiftClient.getMasterUrl());
        return getOcpClusterResourceQuotaToClusterResourceQuotas(openShiftClient);
    }

    Stream<ClusterResourceQuotas> getOcpClusterResourceQuotaToClusterResourceQuotas(OpenShiftClient ocpClient) {
        String masterUrl = ocpClient.getMasterUrl().toString();
        List<ClusterResourceQuota> clusterResourceQuotaList =
                ocpClient.quotas().clusterResourceQuotas().list().getItems();
        Stream<Tuple<Map<String, Quantity>, Map<String, Quantity>>> clusterHardAndUsed =
                clusterResourceQuotaList.stream().parallel()
                        .map(cls -> new Tuple<>(cls.getFullResourceName(), cls.getStatus()))
                        .map(tuple -> {
                            //status is null if quota is not used, so we have to use both spec and status
                            ClusterResourceQuotaStatus status = tuple.getSecond();
                            String quotaName = tuple.getFirst();
                            try {
                                Map<String, Quantity> hard = status.getTotal().getHard();
                                Map<String, Quantity> used = status.getTotal().getUsed();
                                return new Tuple<>(hard, used);
                            } catch (Exception e) {}
                            return new Tuple<>(null, null);
                        });
        log.debug("ClusterHardAndUsed={}", clusterHardAndUsed.collect(Collectors.toList()));
        return Stream.empty();
    }


}
