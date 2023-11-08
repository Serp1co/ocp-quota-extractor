package com.redhat.quota.extractor.services.providers.login;
import io.fabric8.kubernetes.client.Config;

public interface IOcpAuthConfig {

    Config getConfig();

}
