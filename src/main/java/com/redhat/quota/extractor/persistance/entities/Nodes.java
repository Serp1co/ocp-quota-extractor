package com.redhat.quota.extractor.persistance.entities;

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
public class Nodes extends ExtractorEntity {
    String cluster;
    String codeName;
    String CPU;
    String memory;
    String disk;
}
