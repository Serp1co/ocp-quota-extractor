package com.redhat.quota.extractor.utils;

import com.redhat.quota.extractor.persistance.models.Namespaces;

public class ApiToEntity {

    public static Namespaces NAMESPACES(String nsName, String cluster) {
        return Namespaces.builder()
                .namespaceName(nsName)
                .cluster(cluster)
                .build();
    }


}
