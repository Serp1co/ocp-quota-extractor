package com.redhat.quota.extractor.persistance.entities;

import com.redhat.quota.extractor.persistance.entities.commons.ExtractorEntity;
import jakarta.persistence.Entity;
import lombok.Getter;
import lombok.Setter;

@Entity
@Getter
@Setter
public class Annotations extends ExtractorEntity {
    String Namespace;
    String AnnotationName;
    String AnnotationValue;
}
