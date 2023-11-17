package com.redhat.quota.extractor.collectors;

import com.redhat.quota.extractor.entities.Labels;
import io.fabric8.openshift.client.OpenShiftClient;
import lombok.extern.slf4j.Slf4j;

import java.util.stream.Stream;

@Slf4j
public class LabelsCollector implements ICollector<Labels> {
    @Override
    public Stream<Labels> collect(OpenShiftClient openShiftClient, String... namespaces) {
        return null;
    }

}
