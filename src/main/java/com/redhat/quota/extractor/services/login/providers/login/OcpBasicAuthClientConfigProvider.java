package com.redhat.quota.extractor.services.login.providers.login;

import com.redhat.quota.extractor.services.login.exceptions.LoginException;
import io.fabric8.kubernetes.client.Config;
import io.quarkus.arc.DefaultBean;
import io.quarkus.arc.lookup.LookupIfProperty;
import io.quarkus.arc.profile.IfBuildProfile;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.enterprise.inject.Produces;
import jakarta.inject.Inject;

@LookupIfProperty(name = "extractor.client.login.auth", stringValue = "basic-auth")
@ApplicationScoped
public class OcpBasicAuthClientConfigProvider {

    @Inject
    OcpBasicAuthLoginService ocpBasicAuthLoginService;

    @Produces
    @IfBuildProfile("prod")
    Config ocpBasicProdConfigBuilders() throws LoginException {
        String oauthToken = ocpBasicAuthLoginService.login();
        return LoginProviderUtils.builderForBasicProd(oauthToken).build();
    }

    @Produces
    @IfBuildProfile("dev")
    @DefaultBean
    Config ocpBasicDevConfigBuilder() throws LoginException {
        String oauthToken = ocpBasicAuthLoginService.login();
        return LoginProviderUtils.builderForBasicDev(oauthToken).build();
    }

}
