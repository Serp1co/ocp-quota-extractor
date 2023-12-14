package com.redhat.quota.extractor.collectors;

import com.redhat.quota.extractor.entities.AppliedClusterResourceQuotas;
import com.redhat.quota.extractor.entities.AppliedClusterResourceQuotas.AppliedClusterResourceQuotasBuilder;
import io.fabric8.kubernetes.api.model.Quantity;
import io.fabric8.openshift.api.model.ClusterResourceQuota;
import io.fabric8.openshift.api.model.ClusterResourceQuotaSpec;
import io.fabric8.openshift.api.model.ClusterResourceQuotaStatus;
import io.fabric8.openshift.client.OpenShiftClient;
import jakarta.enterprise.context.ApplicationScoped;
import lombok.extern.slf4j.Slf4j;

import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@ApplicationScoped
@Slf4j
public class AppliedClusterResourceQuotasCollector extends ACollector implements ICollector<AppliedClusterResourceQuotas> {

    @Override
    public List<AppliedClusterResourceQuotas> collect(OpenShiftClient openShiftClient, String... namespaces) {
        log.info("collecting AppliedClusterResourceQuotas for cluster {}", openShiftClient.getMasterUrl());
        List<AppliedClusterResourceQuotas> clusterResourceQuotasStream =
                getClusterResourceQuotaStream(openShiftClient).collect(Collectors.toList());
        persist(clusterResourceQuotasStream);
        return clusterResourceQuotasStream;
    }

    Stream<AppliedClusterResourceQuotas> getClusterResourceQuotaStream(OpenShiftClient ocpClient) {
        List<ClusterResourceQuota> clusterResourceQuotaList =
                ocpClient.quotas().clusterResourceQuotas().list().getItems();
        return clusterResourceQuotaList.stream().parallel()
                .map(this::getNameAndStatusOrSpec)
                .map(this::getQuotaFromSpecOrStatus);
    }

    Tuple<String, ?> getNameAndStatusOrSpec(ClusterResourceQuota clusterResourceQuota) {
        if (Optional.ofNullable(clusterResourceQuota.getStatus()).isPresent()) {
            return new Tuple<>(clusterResourceQuota.getMetadata().getName(), clusterResourceQuota.getStatus());
        } else {
            return new Tuple<>(clusterResourceQuota.getMetadata().getName(), clusterResourceQuota.getSpec());
        }
    }

    AppliedClusterResourceQuotas getQuotaFromSpecOrStatus(Tuple<String, ?> tuple) {
        String quotaName = tuple.getFirst();
        AppliedClusterResourceQuotasBuilder builder = AppliedClusterResourceQuotas.builder().AppliedClusterResourceQuotaName(quotaName);
        if (tuple.getSecond() instanceof ClusterResourceQuotaSpec spec) {
            builder = fromSpec(spec, builder);
        }
        if (tuple.getSecond() instanceof ClusterResourceQuotaStatus status) {
            builder = fromStatus(status, builder);
        }
        return builder.build();
    }

    AppliedClusterResourceQuotasBuilder fromSpec(ClusterResourceQuotaSpec spec, AppliedClusterResourceQuotasBuilder builder) {
        Map<String, Quantity> hard = spec.getQuota().getHard();
        builder.HardPods(hard.get("pods") != null ? hard.get("pods").getNumericalAmount() : null);
        builder.HardSecrets(hard.get("secrets") != null ? hard.get("secrets").getNumericalAmount() : null);
        return builder;
    }

    AppliedClusterResourceQuotasBuilder fromStatus(ClusterResourceQuotaStatus status, AppliedClusterResourceQuotasBuilder builder) {
        Map<String, Quantity> used = status.getTotal().getUsed();
        builder.LimitsCPU(used.get("limits.cpu") != null ? used.get("limits.cpu").getNumericalAmount() : null);
        builder.RequestMemory(used.get("requests.memory") != null ?
                used.get("requests.memory").getNumericalAmount() : null);
        builder.UsedLimitCPU(used.get("limits.cpu") != null ? used.get("limits.cpu").getNumericalAmount() : null);
        builder.UsedLimitMemory(used.get("limits.memory") != null ? used.get("limits.memory").getNumericalAmount() : null);
        builder.UsedRequestCPU(used.get("requests.cpu") != null ? used.get("requests.cpu").getNumericalAmount() : null);
        builder.UsedRequestMemory(used.get("requests.memory") != null ? used.get("requests.memory").getNumericalAmount() : null);
        builder.UsedPods(used.get("pods") != null ? used.get("pods").getNumericalAmount() : null);
        builder.UsedSecrets(used.get("secrets") != null ? used.get("secrets").getNumericalAmount() : null);
        return builder;
    }

}