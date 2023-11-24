package com.redhat.quota.extractor.collectors;

import com.redhat.quota.extractor.entities.Labels;
import io.fabric8.openshift.client.OpenShiftClient;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.transaction.Transactional;
import lombok.extern.slf4j.Slf4j;

import java.util.List;

@ApplicationScoped
@Slf4j
public class LabelsCollector extends ACollector implements ICollector<Labels> {
    @Override
    @Transactional
    public List<Labels> collect(OpenShiftClient openShiftClient, String... namespaces) {
        return null;
    }

}
