package com.redhat.quota.extractor.services.providers;

import com.redhat.quota.extractor.services.providers.login.IOcpAuthConfig;
import io.fabric8.kubernetes.client.Config;
import io.fabric8.kubernetes.client.ConfigBuilder;
import io.fabric8.kubernetes.client.KubernetesClient;
import io.fabric8.kubernetes.client.KubernetesClientBuilder;
import io.fabric8.openshift.client.OpenShiftClient;
import jakarta.enterprise.context.RequestScoped;
import jakarta.enterprise.inject.Instance;
import jakarta.enterprise.inject.Produces;
import jakarta.inject.Inject;
import org.eclipse.microprofile.config.inject.ConfigProperty;

import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

public class OpenshiftClientProvider {

    @Inject
    @ConfigProperty(name = "extractor.client.clusters-url")
    List<String> clusters;

    @Produces
    @RequestScoped
    public Set<OpenShiftClient> ocpClients(Instance<IOcpAuthConfig> ocpConfig) {
        return clusters.stream().map(currentCluster -> {
            Config finalConfig = new ConfigBuilder(ocpConfig.get().getConfig())
                    .withMasterUrl(currentCluster)
                    .build();
            //we ignore auto-closable
            KubernetesClient openShiftClient = new KubernetesClientBuilder()
                    .withConfig(finalConfig)
                    .build();
            return openShiftClient.adapt(OpenShiftClient.class);
        }).collect(Collectors.toSet());
    }

}

