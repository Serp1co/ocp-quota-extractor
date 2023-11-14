package com.redhat.quota.extractor.persistance.entities;

import com.redhat.quota.extractor.persistance.entities.commons.ExtractorEntity;
import jakarta.persistence.Entity;
import lombok.*;

@Entity
@Getter
@Setter
@ToString
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class QuotaNamespaces extends ExtractorEntity {
    String Namespace;
    String ClusterResourceQuota;
}
