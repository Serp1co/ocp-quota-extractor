package com.redhat.quota.extractor.collectors;

import com.redhat.quota.extractor.entities.Annotations;
import com.redhat.quota.extractor.entities.ClusterResourceQuotas;
import com.redhat.quota.extractor.entities.ClusterResourceQuotas.ClusterResourceQuotasBuilder;
import com.redhat.quota.extractor.entities.Labels;
import io.fabric8.kubernetes.api.model.ObjectMeta;
import io.fabric8.kubernetes.api.model.Quantity;
import io.fabric8.openshift.api.model.ClusterResourceQuota;
import io.fabric8.openshift.api.model.ClusterResourceQuotaSelector;
import io.fabric8.openshift.api.model.ClusterResourceQuotaSpec;
import io.fabric8.openshift.api.model.ClusterResourceQuotaStatus;
import io.fabric8.openshift.client.OpenShiftClient;
import jakarta.enterprise.context.ApplicationScoped;
import lombok.extern.slf4j.Slf4j;
import org.eclipse.microprofile.config.inject.ConfigProperty;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@ApplicationScoped
@Slf4j
public class ClusterResourceQuotasCollector extends ACollector implements ICollector<ClusterResourceQuotas> {


    @ConfigProperty(name = "extractor.selector-prefix")
    String SELECTOR_PREFIX;

    @Override
    public List<ClusterResourceQuotas> collect(OpenShiftClient openShiftClient, String... namespaces) {
        log.info("collecting ClusterResourceQuotas for cluster {}", openShiftClient.getMasterUrl());
        List<ClusterResourceQuotas> clusterResourceQuotasStream =
                getClusterResourceQuotaStream(openShiftClient).collect(Collectors.toList());
        persist(clusterResourceQuotasStream);
        return clusterResourceQuotasStream;
    }

    Stream<ClusterResourceQuotas> getClusterResourceQuotaStream(OpenShiftClient ocpClient) {
        List<ClusterResourceQuota> clusterResourceQuotaList =
                ocpClient.quotas().clusterResourceQuotas().list().getItems();
        return clusterResourceQuotaList.stream().parallel()
                .map(this::getNameAndSpec)
                .map(this::getQuotaFromStatus);
    }

    Tuple<ClusterResourceQuotasBuilder, Optional<ClusterResourceQuotaStatus>> getNameAndSpec(ClusterResourceQuota clusterResourceQuota) {
        ObjectMeta metadata = clusterResourceQuota.getMetadata();
        List<Labels> labels = new ArrayList<>() {{
            metadata.getLabels().forEach((k,v) -> this.add(Labels.builder().LabelName(k).LabelValue(v).build()));
        }};
        List<Annotations> annotations = new ArrayList<>() {{
            metadata.getAnnotations().forEach((k,v) ->
                    this.add(Annotations.builder().AnnotationName(k).AnnotationValue(v).build()));
        }};
        ClusterResourceQuotasBuilder builder = fromSpec(metadata.getName(), clusterResourceQuota.getSpec())
                .labels(labels)
                .annotations(annotations);
        return new Tuple<>(builder, Optional.ofNullable(clusterResourceQuota.getStatus()));
    }

    ClusterResourceQuotas getQuotaFromStatus(Tuple<ClusterResourceQuotasBuilder, Optional<ClusterResourceQuotaStatus>> tuple) {
        ClusterResourceQuotasBuilder builder = tuple.getFirst();
        Optional<ClusterResourceQuotaStatus> optionalStatus = tuple.getSecond();
        optionalStatus.ifPresent(status -> addStatus(status, builder));
        return builder.build();
    }

    ClusterResourceQuotasBuilder fromSpec(String quotaName, ClusterResourceQuotaSpec spec) {
        Map<String, Quantity> hard = spec.getQuota().getHard();
        ClusterResourceQuotaSelector quotaSelector = spec.getSelector();
        ClusterResourceQuotasBuilder builder = ClusterResourceQuotas.builder()
                .ClusterResourceQuotaName(quotaName)
                .HardPods(hard.get("pods") != null ? hard.get("pods").getNumericalAmount() : null)
                .HardSecrets(hard.get("secrets") != null ? hard.get("secrets").getNumericalAmount() : null);
        if(quotaSelector.getLabels() != null) {
            builder.Ambito(quotaSelector.getLabels().getMatchLabels().get(SELECTOR_PREFIX + "/ambito"))
                    .Application(quotaSelector.getLabels().getMatchLabels().get(SELECTOR_PREFIX + "/application"))
                    .ServiceModel(quotaSelector.getLabels().getMatchLabels().get(SELECTOR_PREFIX + "/servicemodel"))
            ;
        }
        return builder;

    }

    void addStatus(ClusterResourceQuotaStatus status, ClusterResourceQuotasBuilder builder) {
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
    }

}
