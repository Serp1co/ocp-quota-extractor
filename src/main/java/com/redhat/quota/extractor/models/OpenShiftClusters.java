package com.redhat.quota.extractor.models;

import io.quarkus.hibernate.orm.panache.PanacheEntity;
import jakarta.persistence.Entity;
import lombok.Getter;
import lombok.Setter;

@Entity
@Getter
@Setter
public class OpenShiftClusters extends PanacheEntity {
    String ClusterName;
    String ApiUrl;
    String Status;
    String Environment;
    String AppCode;
    String Customer;
}
