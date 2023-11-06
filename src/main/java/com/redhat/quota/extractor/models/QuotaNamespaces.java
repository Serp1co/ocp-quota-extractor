package com.redhat.quota.extractor.models;

import io.quarkus.hibernate.orm.panache.PanacheEntity;
import jakarta.persistence.Entity;
import lombok.*;

@Entity
@Getter
@Setter
@ToString
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class QuotaNamespaces extends PanacheEntity {
    String Namespace;
    String ClusterResourceQuota;
}
