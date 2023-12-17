package com.redhat.quota.extractor.entities.crq;

import com.redhat.quota.extractor.entities.commons.ExtractorEntity;
import jakarta.persistence.*;
import lombok.*;

import java.io.Serializable;

@Entity
@Getter
@Setter
@ToString
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Labels extends ExtractorEntity {
    @Column(columnDefinition="text")
    String labelName;
    @Column(columnDefinition="text")
    String labelValue;

    @ManyToOne
    ClusterResourceQuotas clusterResourceQuotas;

}
