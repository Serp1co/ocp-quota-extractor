package com.redhat.quota.extractor.utils;

import com.redhat.quota.extractor.models.Namespaces;

import java.util.Base64;

public class ApiToEntity {

    public static Namespaces NAMESPACES(String ns_name) {
        return Namespaces.builder()
                .namespaceName(ns_name)
                .build();
    }

    public static String getBasicAuthString(String username, String password) {
        return "Basic " + Base64.getEncoder()
                .encodeToString((username + ":" + password).getBytes());
    }

}
