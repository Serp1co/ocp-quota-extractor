package com.redhat.utils;

import com.redhat.models.Namespaces;

import java.util.Base64;

public class ApiToEntity {

    public static Namespaces NAMESPACES(String ns_name, String cluster_name) {
        return Namespaces.builder()
                .namespaceName(ns_name)
                .cluster(cluster_name)
                .build();
    }

    public static String getBasicAuthString(String username, String password) {
        return "Basic " + Base64.getEncoder()
                .encodeToString((username + ":" + password).getBytes());
    }

}
