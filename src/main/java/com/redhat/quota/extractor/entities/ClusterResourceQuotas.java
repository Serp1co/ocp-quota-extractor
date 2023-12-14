package com.redhat.quota.extractor.entities;

import com.redhat.quota.extractor.entities.commons.ExtractorEntity;
import io.smallrye.common.annotation.Blocking;
import jakarta.persistence.Entity;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.OneToMany;
import jakarta.transaction.Transactional;
import lombok.*;

import java.math.BigDecimal;
import java.util.List;
import java.util.stream.Stream;

@Entity
@Getter
@Setter
@ToString
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ClusterResourceQuotas extends ExtractorEntity {
    String Cluster;
    String ClusterResourceQuotaName;
    String RequestID;
    BigDecimal LimitsCPU;
    BigDecimal RequestMemory;
    BigDecimal UsedLimitCPU;
    BigDecimal UsedLimitMemory;
    BigDecimal UsedRequestCPU;
    BigDecimal UsedRequestMemory;
    BigDecimal HardPods;
    BigDecimal HardSecrets;
    BigDecimal UsedPods;
    BigDecimal UsedSecrets;
    String Ambito; //??
    String Application; //??
    String ServiceModel; //??

    @OneToMany
    @JoinColumn(name = "clusterresourcequotas_id")
    List<Labels> labels;

    @OneToMany
    @JoinColumn(name = "clusterresourcequotas_id")
    List<Annotations> annotations;

    @Override
    public void persist() {
        persist(labels);
        persist(annotations);
        super.persist();
    }

}