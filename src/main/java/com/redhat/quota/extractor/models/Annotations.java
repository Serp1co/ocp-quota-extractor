package com.redhat.quota.extractor.models;

import io.quarkus.hibernate.orm.panache.PanacheEntity;
import jakarta.persistence.Entity;
import lombok.Getter;
import lombok.Setter;

@Entity
@Getter
@Setter
public class Annotations extends PanacheEntity {
    String Namespace;
    String AnnotationName;
    String AnnotationValue;
}
