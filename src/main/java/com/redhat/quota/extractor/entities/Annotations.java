package com.redhat.quota.extractor.entities;

import com.redhat.quota.extractor.entities.commons.ExtractorEntity;
import jakarta.persistence.*;
import lombok.*;

@Entity
@Getter
@Setter
@ToString
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Annotations extends ExtractorEntity {
    String Namespace;
    @Column(columnDefinition="text")
    String AnnotationName;
    @Column(columnDefinition="text")
    String AnnotationValue;
    @ManyToOne
    ClusterResourceQuotas clusterResourceQuotas;
}
