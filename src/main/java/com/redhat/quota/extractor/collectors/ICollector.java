package com.redhat.quota.extractor.collectors;

import com.redhat.quota.extractor.persistance.entities.commons.ExtractorEntity;
import io.fabric8.openshift.client.OpenShiftClient;

import java.util.stream.Stream;

@FunctionalInterface
public interface ICollector {

    Stream<? extends ExtractorEntity> collect(OpenShiftClient openShiftClient);

}
