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
public class Nodes extends ExtractorEntity {
    String cluster;
    String nodeName;
    BigDecimal allocatableCPU;
    BigDecimal allocatableMemory;
    BigDecimal allocatableDisk;
    BigDecimal capacityCPU;
    BigDecimal capacityMemory;
    BigDecimal capacityDisk;
}
