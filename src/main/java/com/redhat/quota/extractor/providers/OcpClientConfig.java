package com.redhat.quota.extractor.providers;

import io.fabric8.kubernetes.client.Config;
import io.fabric8.kubernetes.client.ConfigBuilder;
import lombok.Getter;
import lombok.NoArgsConstructor;

/**
 * Container class for injecting io.fabric8.kubernetes.client.Config
 */
@Getter
public class OcpClientConfig {

    Config config;

    public OcpClientConfig(Config config) {
        this.config = new ConfigBuilder(config).build();
    }

    public OcpClientConfig(OcpClientConfig config) {
        this.config = new ConfigBuilder(config.getConfig()).build();
    }

    @Getter
    public static class OcpClientConfigBuilder {

        ConfigBuilder cf;

        public OcpClientConfigBuilder() {
            this.cf = new ConfigBuilder();
        }

        public OcpClientConfigBuilder(OcpClientConfig ocpClientConfig) {
            this.cf = new ConfigBuilder(ocpClientConfig.getConfig());
        }

        public OcpClientConfigBuilder applyNoSsl() {
            cf.withDisableHostnameVerification(true).withTrustCerts(true);
            return this;
        }

        public OcpClientConfigBuilder applyOauth(String token) {
            cf.withOauthToken(token);
            return this;
        }

        public OcpClientConfig build() {
            return new OcpClientConfig(cf.build());
        }

    }

}
