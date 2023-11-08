package com.redhat.quota.extractor.persistance.models;

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
public class Namespaces extends PanacheEntity {
    String namespaceName;
    String cluster;
}
