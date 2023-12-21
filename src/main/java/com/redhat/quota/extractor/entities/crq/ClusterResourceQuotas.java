package com.redhat.quota.extractor.entities.crq;

import com.redhat.quota.extractor.entities.commons.ExtractorEntity;
import jakarta.persistence.*;
import lombok.*;

import java.math.BigDecimal;
import java.util.List;

@Entity
@Getter
@Setter
@ToString
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ClusterResourceQuotas extends ExtractorEntity {
    String cluster;
    String clusterResourceQuotaName;
    String orderId;
    BigDecimal limitsCPU;
    BigDecimal requestMemory;
    BigDecimal usedLimitCPU;
    BigDecimal usedLimitMemory;
    BigDecimal usedRequestCPU;
    BigDecimal usedRequestMemory;
    BigDecimal hardPods;
    BigDecimal hardSecrets;
    BigDecimal usedPods;
    BigDecimal usedSecrets;
    String ambito;
    String application;
    String serviceModel;

    @OneToMany(cascade = CascadeType.ALL, orphanRemoval = true)
    @JoinColumn(name = "clusterresourcequotas_id")
    List<Labels> labels;

    @OneToMany(cascade = CascadeType.ALL, orphanRemoval = true)
    @JoinColumn(name = "clusterresourcequotas_id")
    List<Annotations> annotations;

    @Override
    public void persist() {
        persist(labels);
        persist(annotations);
        super.persist();
    }

}
