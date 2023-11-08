package com.redhat.quota.extractor.services.login.providers.login;

import io.fabric8.kubernetes.client.Config;
import io.quarkus.arc.DefaultBean;
import io.quarkus.arc.lookup.LookupIfProperty;
import io.quarkus.arc.profile.IfBuildProfile;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.enterprise.inject.Produces;

@LookupIfProperty(name = "extractor.client.login.auth", stringValue = "service-account")
@ApplicationScoped
public class OcpSaAuthClientConfigProvider {

    @Produces
    @IfBuildProfile("prod")
    Config ocpSaProdConfigBuilder() {
        return LoginProviderUtils.builderForSaProd().build();
    }

    @Produces
    @IfBuildProfile("dev")
    Config ocpSaDevConfigBuilder() {
        return LoginProviderUtils.builderForSaDev().build();
    }

}
