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
public class OpenShiftClusters extends PanacheEntity {
    String ClusterName;
    String ApiUrl;
    String Status;
    String Environment;
    String AppCode;
    String Customer;
}
