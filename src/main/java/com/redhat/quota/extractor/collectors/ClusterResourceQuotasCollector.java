package com.redhat.quota.extractor.collectors;

import com.redhat.quota.extractor.entities.ClusterResourceQuotas;
import com.redhat.quota.extractor.entities.ClusterResourceQuotas.ClusterResourceQuotasBuilder;
import io.fabric8.kubernetes.api.model.Quantity;
import io.fabric8.openshift.api.model.ClusterResourceQuota;
import io.fabric8.openshift.api.model.ClusterResourceQuotaSpec;
import io.fabric8.openshift.api.model.ClusterResourceQuotaStatus;
import io.fabric8.openshift.client.OpenShiftClient;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.transaction.Transactional;
import lombok.extern.slf4j.Slf4j;

import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@ApplicationScoped
@Slf4j
public class ClusterResourceQuotasCollector extends ACollector implements ICollector<ClusterResourceQuotas> {

    @Override
    public List<ClusterResourceQuotas> collect(OpenShiftClient openShiftClient, String... namespaces) {
        log.info("collecting ResourceQuotas for cluster {}", openShiftClient.getMasterUrl());
        List<ClusterResourceQuotas> clusterResourceQuotasStream =
                getClusterResourceQuotaStream(openShiftClient).collect(Collectors.toList());
        persist(clusterResourceQuotasStream);
        return clusterResourceQuotasStream;
    }

    Stream<ClusterResourceQuotas> getClusterResourceQuotaStream(OpenShiftClient ocpClient) {
        List<ClusterResourceQuota> clusterResourceQuotaList =
                ocpClient.quotas().clusterResourceQuotas().list().getItems();
        return clusterResourceQuotaList.stream().parallel()
                .map(this::getNameAndStatusOrSpec)
                .map(this::getQuotaFromSpecOrStatus);
    }

    Tuple<String, ?> getNameAndStatusOrSpec(ClusterResourceQuota clusterResourceQuota) {
        //status is null if quota is not used, so we have to use both spec and status
        if (Optional.ofNullable(clusterResourceQuota.getStatus()).isPresent()) {
            return new Tuple<>(clusterResourceQuota.getFullResourceName(), clusterResourceQuota.getStatus());
        } else {
            return new Tuple<>(clusterResourceQuota.getFullResourceName(), clusterResourceQuota.getSpec());
        }
    }

    ClusterResourceQuotas getQuotaFromSpecOrStatus(Tuple<String, ?> tuple) {
        String quotaName = tuple.getFirst();
        ClusterResourceQuotasBuilder builder =
                ClusterResourceQuotas.builder().ClusterResourceQuotaName(quotaName);
        if (tuple.getSecond() instanceof ClusterResourceQuotaSpec spec) {
            Map<String, Quantity> hard = spec.getQuota().getHard();
            builder.HardPods(hard.get("pods").getNumericalAmount());
            builder.HardSecrets(hard.get("secrets").getNumericalAmount());
        }
        if (tuple.getSecond() instanceof ClusterResourceQuotaStatus status) {
            Map<String, Quantity> hard = status.getTotal().getHard();
            Map<String, Quantity> used = status.getTotal().getUsed();
            builder.HardPods(hard.get("pods").getNumericalAmount());
            builder.HardSecrets(hard.get("secrets").getNumericalAmount());
            builder.UsedPods(used.get("pods").getNumericalAmount());
            builder.UsedSecrets(used.get("secrets").getNumericalAmount());
        }
        return builder.build();
    }

}
