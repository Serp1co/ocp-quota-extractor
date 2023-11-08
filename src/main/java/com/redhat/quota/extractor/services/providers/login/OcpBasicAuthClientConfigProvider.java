package com.redhat.quota.extractor.services.providers.login;

import com.redhat.quota.extractor.services.exceptions.LoginException;
import io.fabric8.kubernetes.client.Config;
import io.quarkus.arc.lookup.LookupIfProperty;
import jakarta.enterprise.context.Dependent;
import jakarta.inject.Inject;

@LookupIfProperty(name = "extractor.client.login.auth", stringValue = "basic-auth")
@Dependent
public class OcpBasicAuthClientConfigProvider implements IOcpAuthConfig {

    @Inject
    OcpBasicAuthLoginService ocpBasicAuthLoginService;

    Config ocpBasicProdConfigBuilders() throws LoginException {
        String oauthToken = ocpBasicAuthLoginService.login();
        return OcpAuthUtils.builderForBasicProd(oauthToken).build();
    }

    Config ocpBasicDevConfigBuilder() throws LoginException {
        String oauthToken = ocpBasicAuthLoginService.login();
        return OcpAuthUtils.builderForBasicDev(oauthToken).build();
    }

    @Override
    public Config getConfig() {
        return ocpBasicDevConfigBuilder();
    }

}
