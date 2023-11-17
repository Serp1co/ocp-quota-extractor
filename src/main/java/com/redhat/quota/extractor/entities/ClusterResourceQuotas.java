package com.redhat.quota.extractor.entities;

import com.redhat.quota.extractor.entities.commons.ExtractorEntity;
import jakarta.persistence.Entity;
import lombok.*;

@Entity
@Getter
@Setter
@ToString
@Builder
@NoArgsConstructor
@AllArgsConstructor
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
    String Ambito; //??
    String Application; //??
    String ServiceModel; //??
}
