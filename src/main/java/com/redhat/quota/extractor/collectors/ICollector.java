package com.redhat.quota.extractor.collectors;

import com.redhat.quota.extractor.entities.commons.ExtractorEntity;
import io.fabric8.openshift.client.OpenShiftClient;
import io.smallrye.common.annotation.Blocking;

import java.util.List;
import java.util.stream.Stream;

public interface ICollector<T> {

    Stream<T> collect(OpenShiftClient openShiftClient, String... namespaces);

    @Blocking
    default Void persist(List<? extends ExtractorEntity> entities) {
        ExtractorEntity.persist(entities);
        return null;
    }

}
