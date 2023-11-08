package com.redhat.quota.extractor.services.providers.login;

import io.fabric8.kubernetes.client.Config;
import io.quarkus.arc.lookup.LookupIfProperty;
import jakarta.enterprise.context.Dependent;

@LookupIfProperty(name = "extractor.client.login.auth", stringValue = "service-account")
@Dependent
public class OcpSaAuthClientConfigProvider implements IOcpAuthConfig {

    Config ocpSaProdConfigBuilder() {
        return OcpAuthUtils.builderForSaProd().build();
    }

    Config ocpSaDevConfigBuilder() {
        return OcpAuthUtils.builderForSaDev().build();
    }

    @Override
    public Config getConfig() {
        return ocpSaDevConfigBuilder();
    }

}
