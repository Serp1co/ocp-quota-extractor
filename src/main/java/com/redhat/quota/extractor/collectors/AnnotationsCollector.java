package com.redhat.quota.extractor.collectors;

import com.redhat.quota.extractor.entities.Annotations;
import io.fabric8.openshift.client.OpenShiftClient;
import jakarta.enterprise.context.ApplicationScoped;
import lombok.extern.java.Log;
import lombok.extern.slf4j.Slf4j;

import java.util.stream.Stream;

@ApplicationScoped
@Slf4j
public class AnnotationsCollector implements ICollector<Annotations> {
    @Override
    public Stream<Annotations> collect(OpenShiftClient openShiftClient, String... namespaces) {
        return null;
    }
}
