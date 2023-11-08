package com.redhat.quota.extractor.persistance.models;

import io.quarkus.hibernate.orm.panache.PanacheEntity;
import jakarta.persistence.Entity;
import lombok.Getter;
import lombok.Setter;

@Entity
@Getter
@Setter
public class Labels extends PanacheEntity {
    String Namespace;
    String LabelName;
    String LabelValue;
}
