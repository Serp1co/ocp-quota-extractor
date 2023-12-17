package com.redhat.quota.extractor.entities.crq;

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
    @Column(columnDefinition="text")
    String annotationName;
    @Column(columnDefinition="text")
    String annotationValue;
    @ManyToOne
    ClusterResourceQuotas clusterResourceQuotas;
}
