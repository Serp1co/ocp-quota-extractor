package com.redhat.models;

import io.quarkus.hibernate.orm.panache.PanacheEntity;
import jakarta.persistence.Entity;
import lombok.Getter;
import lombok.Setter;

@Entity
@Getter
@Setter
public class QuotaNamespaces extends PanacheEntity {
    String Namespace;
    String ClusterResourceQuota;
}
