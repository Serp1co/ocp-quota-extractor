package com.redhat.quota.extractor.entities;

import com.redhat.quota.extractor.entities.commons.ExtractorEntity;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToMany;
import lombok.*;

@Entity
@Getter
@Setter
@ToString
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Labels extends ExtractorEntity {
    String Namespace;
    @Column(columnDefinition="text")
    String LabelName;
    @Column(columnDefinition="text")
    String LabelValue;

    @ManyToOne
    ClusterResourceQuotas clusterResourceQuotas;

}
