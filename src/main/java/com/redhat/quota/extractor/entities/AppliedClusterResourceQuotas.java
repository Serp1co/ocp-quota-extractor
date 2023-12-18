package com.redhat.quota.extractor.entities;

import com.redhat.quota.extractor.entities.commons.ExtractorEntity;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import jakarta.persistence.UniqueConstraint;
import lombok.*;

@Entity
@Getter
@Setter
@ToString
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class AppliedClusterResourceQuotas extends ExtractorEntity {
    String namespace;
    String clusterResourceQuotaName;
}