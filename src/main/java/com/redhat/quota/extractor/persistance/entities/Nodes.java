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
public class Nodes extends ExtractorEntity {
    String cluster;
    String codeName;
    String CPU;
    String memory;
    String disk;
}
