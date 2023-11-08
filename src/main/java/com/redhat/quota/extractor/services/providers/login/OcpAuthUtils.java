package com.redhat.quota.extractor.services.providers.login;

import io.fabric8.kubernetes.client.ConfigBuilder;

public class OcpAuthUtils {

    static ConfigBuilder builderForSaProd() {
        return new ConfigBuilder();
    }

    static ConfigBuilder builderForSaDev() {
        return new ConfigBuilder()
                .withDisableHostnameVerification(true)
                .withTrustCerts(true);
    }

    static ConfigBuilder builderForBasicProd(String token) {
        return builderForSaProd()
                .withOauthToken(token);
    }

    static ConfigBuilder builderForBasicDev(String token) {
        return builderForSaDev()
                .withOauthToken(token);
    }

}
