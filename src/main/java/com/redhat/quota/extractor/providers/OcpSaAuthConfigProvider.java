package com.redhat.quota.extractor.providers;

import io.quarkus.arc.lookup.LookupIfProperty;
import jakarta.enterprise.context.Dependent;
import jakarta.enterprise.inject.Produces;
import lombok.extern.java.Log;

/**
 * bean producer for ocp client with service account auth
 */
@LookupIfProperty(name = "extractor.client.login.auth", stringValue = "service-account")
@Dependent
@Log
public class OcpSaAuthConfigProvider {

    @Produces
    @LookupIfProperty(name = "extractor.client.login.ssl", stringValue = "true")
    OcpClientConfig ocpSaSsl() {
        log.finer("applying Service Account Auth with SSL enabled");
        return new OcpClientConfig.OcpClientConfigBuilder().build();
    }

    @Produces
    @LookupIfProperty(name = "extractor.client.login.ssl", stringValue = "false")
    OcpClientConfig ocpSaNoSsl() {
        log.finer("applying Service Account Auth with SSL disabled");
        return new OcpClientConfig.OcpClientConfigBuilder().applyNoSsl().build();
    }

}
