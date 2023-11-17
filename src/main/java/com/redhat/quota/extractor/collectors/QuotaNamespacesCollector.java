package com.redhat.quota.extractor.collectors;

import com.redhat.quota.extractor.entities.QuotaNamespaces;
import io.fabric8.openshift.client.OpenShiftClient;
import jakarta.enterprise.context.ApplicationScoped;
import lombok.Getter;
import lombok.extern.java.Log;
import lombok.extern.slf4j.Slf4j;

import java.util.stream.Stream;

@Getter
@ApplicationScoped
@Slf4j
public class QuotaNamespacesCollector implements ICollector<QuotaNamespaces> {

    @Override
    public Stream<QuotaNamespaces> collect(OpenShiftClient openShiftClient, String[] namespaces) {
        return null;
    }


    static Stream<QuotaNamespaces> getOcpQuotaByNamespaceToQuotaNamespacesEntity(OpenShiftClient ocpClient, String[] namespaces) {
        String masterUrl = ocpClient.getMasterUrl().toString();
        //todo
        return null;
    }

}


