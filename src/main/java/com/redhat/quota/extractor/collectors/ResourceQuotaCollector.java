package com.redhat.quota.extractor.collectors;

import com.redhat.quota.extractor.entities.ResourceQuotas;
import io.fabric8.openshift.client.OpenShiftClient;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.transaction.Transactional;
import lombok.extern.slf4j.Slf4j;

import java.util.List;

@ApplicationScoped
@Slf4j
public class ResourceQuotaCollector implements ICollector<ResourceQuotas> {

    @Override
    @Transactional
    public List<ResourceQuotas> collect(OpenShiftClient openShiftClient, String... namespaces) {
        log.info("collecting ResourceQuotas for cluster {}", openShiftClient.getMasterUrl());
        log.info("ResourceQuotas={}", openShiftClient.resourceQuotas().list().getItems());
        return null;
    }

}