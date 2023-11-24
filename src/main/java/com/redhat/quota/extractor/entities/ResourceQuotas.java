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
public class ResourceQuotas extends ExtractorEntity {
    String Cluster;
    String QuotaName;
    String RequestID;
    BigDecimal LimitsCPU;
    BigDecimal RequestMemory;
    BigDecimal UsedLimitCPU;
    BigDecimal UsedLimitMemory;
    BigDecimal UsedRequestCPU;
    BigDecimal UsedRequestMemory;
    String Ambito; //??
    String Application; //??
    String ServiceModel; //??
}