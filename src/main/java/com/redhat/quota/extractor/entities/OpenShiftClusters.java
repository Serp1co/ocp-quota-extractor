package com.redhat.quota.extractor.entities;

import com.redhat.quota.extractor.entities.commons.ExtractorEntity;
import jakarta.persistence.Entity;
import lombok.*;

@Entity
@Getter
@Setter
@ToString
@Builder
@NoArgsConstructor
@AllArgsConstructor
//tabella di check dei cluster (nota: evo future - usarla per prendere lista cluster da interrogare)
public class OpenShiftClusters extends ExtractorEntity {
    String ClusterName;
    String ApiUrl; //??
    String Status; //??
    String Environment; //??
    String AppCode; //??
    String Customer; //??
}
