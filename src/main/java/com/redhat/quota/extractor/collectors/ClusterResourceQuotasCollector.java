package com.redhat.quota.extractor.collectors;

import com.redhat.quota.extractor.entities.ClusterResourceQuotas;

import io.fabric8.kubernetes.api.model.Quantity;
import io.fabric8.openshift.api.model.ClusterResourceQuota;
import io.fabric8.openshift.api.model.ClusterResourceQuotaStatus;
import io.fabric8.openshift.client.OpenShiftClient;
import jakarta.enterprise.context.ApplicationScoped;
import lombok.Getter;
import lombok.extern.java.Log;
import lombok.extern.slf4j.Slf4j;

import java.util.List;
import java.util.Map;
import java.util.stream.Stream;

@ApplicationScoped
@Slf4j
public class ClusterResourceQuotasCollector implements ICollector<ClusterResourceQuotas> {

    public Stream<ClusterResourceQuotas> collect(OpenShiftClient openShiftClient, String... namespaces) {
            log.info("collecting namespaces for cluster {}", openShiftClient.getMasterUrl());
            return getOcpClusterResourceQuotaToClusterResourceQuotas(openShiftClient);
        }

        static Stream<ClusterResourceQuotas> getOcpClusterResourceQuotaToClusterResourceQuotas(OpenShiftClient ocpClient) {
            class Holder<T> {
                ClusterResourceQuotas out;
                T item;
            }
            String masterUrl = ocpClient.getMasterUrl().toString();
            List<ClusterResourceQuota> clusterResourceQuotaList =
                    ocpClient.quotas().clusterResourceQuotas().list().getItems();
            return clusterResourceQuotaList.stream().parallel()
                    .map(cls -> {
                        Holder<ClusterResourceQuotaStatus> holder = new Holder<>();
                        holder.out = new ClusterResourceQuotas();
                        holder.out.setCluster(masterUrl);
                        holder.out.setQuotaName(cls.getFullResourceName());
                        holder.item = cls.getStatus();
                        return holder;
                    })
                    .map(holder -> {
                        Map<String, Quantity> hard = holder.item.getTotal().getHard();
                        Map<String, Quantity> used = holder.item.getTotal().getHard();
                        return ClusterResourceQuotas.builder()
                                /*.QuotaName()
                                .LimitsCPU()
                                .UsedLimitCPU()
                                .UsedRequestCPU()
                                .RequestMemory()
                                .UsedRequestMemory()
                                .ServiceModel()
                                .RequestID()
                                .Ambito()
                                .Application()*/
                                .build();
                    });
        }
}
