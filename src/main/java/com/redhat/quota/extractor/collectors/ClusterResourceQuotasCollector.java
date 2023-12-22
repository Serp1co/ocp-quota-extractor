package com.redhat.quota.extractor.collectors;

import com.redhat.quota.extractor.entities.crq.Annotations;
import com.redhat.quota.extractor.entities.crq.ClusterResourceQuotas;
import com.redhat.quota.extractor.entities.crq.ClusterResourceQuotas.ClusterResourceQuotasBuilder;
import com.redhat.quota.extractor.entities.crq.Labels;
import com.redhat.quota.extractor.utils.CollectorsUtils;
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

import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@ApplicationScoped
@Slf4j
public class ClusterResourceQuotasCollector extends ACollector implements ICollector<ClusterResourceQuotas> {

    @ConfigProperty(name = "extractor.crq-selector-prefix")
    String SELECTOR_PREFIX;

    @Override
    public List<ClusterResourceQuotas> collect(OpenShiftClient openShiftClient, String... namespaces) {
        log.info("START - collecting ClusterResourceQuotas for cluster={}", openShiftClient.getMasterUrl().toString());
        List<ClusterResourceQuotas> clusterResourceQuotasStream =
                getClusterResourceQuotaStream(openShiftClient).collect(Collectors.toList());
        persist(clusterResourceQuotasStream);
        log.info("END - collecting ClusterResourceQuotas for cluster={}", openShiftClient.getMasterUrl().toString());
        return clusterResourceQuotasStream;
    }

    Stream<ClusterResourceQuotas> getClusterResourceQuotaStream(OpenShiftClient ocpClient) {
        String masterUrl = ocpClient.getMasterUrl().toString();
        List<ClusterResourceQuota> clusterResourceQuotaList =
                ocpClient.quotas().clusterResourceQuotas().list().getItems();
        return clusterResourceQuotaList.stream().parallel()
                .map(clusterResourceQuota -> getNameAndSpec(masterUrl, clusterResourceQuota))
                .map(this::getQuotaFromStatus);
    }

    Tuple<ClusterResourceQuotasBuilder, Optional<ClusterResourceQuotaStatus>> getNameAndSpec(
            String clusterUrl,
            ClusterResourceQuota clusterResourceQuota
    ) {
        ObjectMeta metadata = clusterResourceQuota.getMetadata();
        List<Labels> labels = new ArrayList<>() {{
            metadata.getLabels().forEach((k,v) -> this.add(Labels.builder().labelName(k).labelValue(v).build()));
        }};
        List<Annotations> annotations = new ArrayList<>() {{
            metadata.getAnnotations().forEach((k,v) ->
                    this.add(Annotations.builder().annotationName(k).annotationValue(v).build()));
        }};
        ClusterResourceQuotasBuilder builder = fromSpec(clusterResourceQuota.getSpec())
                .clusterResourceQuotaName(metadata.getName())
                .cluster(clusterUrl)
                .labels(labels)
                .annotations(annotations)
                .orderId(metadata.getAnnotations().get(SELECTOR_PREFIX + "/orderid"));
        return new Tuple<>(builder, Optional.ofNullable(clusterResourceQuota.getStatus()));
    }

    ClusterResourceQuotas getQuotaFromStatus(
            Tuple<ClusterResourceQuotasBuilder,
            Optional<ClusterResourceQuotaStatus>> tuple
    ) {
        ClusterResourceQuotasBuilder builder = tuple.getFirst();
        Optional<ClusterResourceQuotaStatus> optionalStatus = tuple.getSecond();
        optionalStatus.ifPresent(status -> addStatus(status, builder));
        return builder.build();
    }

    ClusterResourceQuotasBuilder fromSpec(ClusterResourceQuotaSpec spec) {
        Map<String, Quantity> hard = spec.getQuota().getHard();
        ClusterResourceQuotaSelector quotaSelector = spec.getSelector();
        ClusterResourceQuotasBuilder builder = ClusterResourceQuotas.builder()
                .hardPods(CollectorsUtils.getNumericalAmountOrNull(hard, "pods"))
                .hardSecrets(CollectorsUtils.getNumericalAmountOrNull(hard, "secrets"))
                .limitsCPU(CollectorsUtils.getNumericalAmountOrNull(hard, "limits.cpu"))
                .requestMemory(CollectorsUtils.getNumericalAmountOrNull(hard, "requests.memory",
                        CollectorsUtils::fromKibToMib));
        if(quotaSelector.getLabels() != null) {
            builder.ambito(quotaSelector.getLabels().getMatchLabels().get(SELECTOR_PREFIX + "/ambito"))
                    .application(quotaSelector.getLabels().getMatchLabels().get(SELECTOR_PREFIX + "/application"))
                    .serviceModel(quotaSelector.getLabels().getMatchLabels().get(SELECTOR_PREFIX + "/servicemodel"))
            ;
        }
        return builder;
    }

    void addStatus(ClusterResourceQuotaStatus status, ClusterResourceQuotasBuilder builder) {
        Map<String, Quantity> used = status.getTotal().getUsed();
        builder.usedLimitCPU(CollectorsUtils.getNumericalAmountOrNull(used, "limits.cpu"))
                .usedLimitMemory(CollectorsUtils.getNumericalAmountOrNull(used, "limits.memory",
                        CollectorsUtils::fromKibToMib))
                .usedRequestCPU(CollectorsUtils.getNumericalAmountOrNull(used, "requests.cpu"))
                .usedRequestMemory(CollectorsUtils.getNumericalAmountOrNull(used, "requests.memory",
                        CollectorsUtils::fromKibToMib))
                .usedPods(CollectorsUtils.getNumericalAmountOrNull(used, "pods"))
                .usedSecrets(CollectorsUtils.getNumericalAmountOrNull(used, "secrets"));
    }


}
