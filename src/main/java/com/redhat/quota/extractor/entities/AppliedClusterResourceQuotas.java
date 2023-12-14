package com.redhat.quota.extractor.entities;

import com.redhat.quota.extractor.entities.commons.ExtractorEntity;
import jakarta.persistence.Entity;
import lombok.*;

import java.math.BigDecimal;

@Entity
@Getter
@Setter
@ToString
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class AppliedClusterResourceQuotas extends ExtractorEntity {
    String Namespace;
    String Cluster;
    String AppliedClusterResourceQuotaName;
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
}