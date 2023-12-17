package com.redhat.quota.extractor.collectors;

import com.redhat.quota.extractor.entities.crq.Annotations;
import com.redhat.quota.extractor.entities.crq.ClusterResourceQuotas;
import com.redhat.quota.extractor.entities.crq.ClusterResourceQuotas.ClusterResourceQuotasBuilder;
import com.redhat.quota.extractor.entities.crq.Labels;
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

    @ConfigProperty(name = "extractor.crq-selector-prefixes")
    String[] SELECTOR_PREFIX;

    @Override
    public List<ClusterResourceQuotas> collect(OpenShiftClient openShiftClient, String... namespaces) {
        List<ClusterResourceQuotas> clusterResourceQuotasStream =
                getClusterResourceQuotaStream(openShiftClient).collect(Collectors.toList());
        persist(clusterResourceQuotasStream);
        return clusterResourceQuotasStream;
    }

    Stream<ClusterResourceQuotas> getClusterResourceQuotaStream(OpenShiftClient ocpClient) {
        String masterUrl = ocpClient.getMasterUrl().toString();
        log.info("collecting ClusterResourceQuotas for cluster {}", masterUrl);
        List<ClusterResourceQuota> clusterResourceQuotaList =
                ocpClient.quotas().clusterResourceQuotas().list().getItems();
        return clusterResourceQuotaList.stream().parallel()
                .map(clusterResourceQuota -> getNameAndSpec(masterUrl, clusterResourceQuota))
                .map(this::getQuotaFromStatus);
    }

    Tuple<ClusterResourceQuotasBuilder, Optional<ClusterResourceQuotaStatus>> getNameAndSpec(String clusterUrl, ClusterResourceQuota clusterResourceQuota) {
        ObjectMeta metadata = clusterResourceQuota.getMetadata();
        List<Labels> labels = new ArrayList<>() {{
            metadata.getLabels().forEach((k,v) -> this.add(Labels.builder().labelName(k).labelValue(v).build()));
        }};
        List<Annotations> annotations = new ArrayList<>() {{
            metadata.getAnnotations().forEach((k,v) ->
                    this.add(Annotations.builder().annotationName(k).annotationValue(v).build()));
        }};
        ClusterResourceQuotasBuilder builder = fromSpec(metadata.getName(), clusterResourceQuota.getSpec())
                .cluster(clusterUrl)
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
                .clusterResourceQuotaName(quotaName)
                .hardPods(hard.get("pods") != null ? hard.get("pods").getNumericalAmount() : null)
                .hardSecrets(hard.get("secrets") != null ? hard.get("secrets").getNumericalAmount() : null)
                .limitsCPU(hard.get("limits.cpu") != null ? hard.get("limits.cpu").getNumericalAmount() : null)
                .requestMemory(hard.get("requests.memory") != null ? hard.get("requests.memory").getNumericalAmount() : null);
        if(quotaSelector.getLabels() != null) {
            for(String selector_prefix : SELECTOR_PREFIX) {
                builder.ambito(quotaSelector.getLabels().getMatchLabels().get(selector_prefix + "/ambito"))
                        .application(quotaSelector.getLabels().getMatchLabels().get(selector_prefix + "/application"))
                        .serviceModel(quotaSelector.getLabels().getMatchLabels().get(selector_prefix + "/servicemodel"))
                ;
            }
        }
        return builder;
    }

    void addStatus(ClusterResourceQuotaStatus status, ClusterResourceQuotasBuilder builder) {
        Map<String, Quantity> used = status.getTotal().getUsed();
        builder.usedLimitCPU(used.get("limits.cpu") != null ? used.get("limits.cpu").getNumericalAmount() : null)
                .usedLimitMemory(used.get("limits.memory") != null ? used.get("limits.memory").getNumericalAmount() : null)
                .usedRequestCPU(used.get("requests.cpu") != null ? used.get("requests.cpu").getNumericalAmount() : null)
                .usedRequestMemory(used.get("requests.memory") != null ? used.get("requests.memory").getNumericalAmount() : null)
                .usedPods(used.get("pods") != null ? used.get("pods").getNumericalAmount() : null)
                .usedSecrets(used.get("secrets") != null ? used.get("secrets").getNumericalAmount() : null);
    }

}
