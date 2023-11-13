package com.redhat.quota.extractor.persistance.entities;

import io.quarkus.hibernate.orm.panache.PanacheEntity;
import jakarta.persistence.Entity;
import lombok.Getter;
import lombok.Setter;

@Entity
@Getter
@Setter
public class ClusterResourceQuotas extends ExtractorEntity {
    String Cluster;
    String QuotaName;
    String RequestID;
    String LimitsCPU;
    String RequestMemory;
    String UsedLimitCPU;
    String UsedLimitMemory;
    String UsedRequestCPU;
    String UsedRequestMemory;
    String Ambito;
    String Application;
    String ServiceModel;
}
