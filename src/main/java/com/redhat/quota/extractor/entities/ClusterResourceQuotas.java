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
public class ClusterResourceQuotas extends ExtractorEntity {
    String ClusterResourceQuotaName;
    BigDecimal HardPods;
    BigDecimal HardSecrets;
    BigDecimal UsedPods;
    BigDecimal UsedSecrets;
}