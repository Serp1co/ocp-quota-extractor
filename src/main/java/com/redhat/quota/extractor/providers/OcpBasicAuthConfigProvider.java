package com.redhat.quota.extractor.providers;

import com.redhat.quota.extractor.exceptions.LoginException;
import com.redhat.quota.extractor.services.OcpBasicAuthLoginService;
import io.quarkus.arc.lookup.LookupIfProperty;
import jakarta.enterprise.context.Dependent;
import jakarta.enterprise.inject.Produces;
import jakarta.inject.Inject;
import lombok.extern.java.Log;


/**
 * bean producer for ocp client with basic auth
 */
@LookupIfProperty(name = "extractor.client.login.auth", stringValue = "basic-auth")
@Dependent
@Log
public class OcpBasicAuthConfigProvider {

    @Inject
    OcpBasicAuthLoginService ocpBasicAuthLoginService;

    @Produces
    @LookupIfProperty(name = "extractor.client.login.ssl", stringValue = "true")
    OcpClientConfig ocpBasicSsl() throws LoginException {
        log.finer("applying Basic Auth with SSL enabled");
        String oauthToken = ocpBasicAuthLoginService.login();
        return new OcpClientConfig.OcpClientConfigBuilder()
                .applyOauth(oauthToken)
                .build();
    }

    @Produces
    @LookupIfProperty(name = "extractor.client.login.ssl", stringValue = "false")
    OcpClientConfig ocpBasicNoSsl() throws LoginException {
        log.finer("applying Basic Auth with SSL disabled");
        String oauthToken = ocpBasicAuthLoginService.login();
        return new OcpClientConfig.OcpClientConfigBuilder()
                .applyOauth(oauthToken)
                .applyNoSsl()
                .build();
    }


}
