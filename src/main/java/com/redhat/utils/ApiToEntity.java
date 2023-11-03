package com.redhat.utils;

import com.redhat.models.Namespaces;

public class ApiToEntity {


    public static Namespaces NAMESPACES(String ns_name, String cluster_name) {
        Namespaces ns = new Namespaces();
        ns.setNamespaceName(ns_name);
        ns.setCluster(cluster_name);
        return ns;
    }

}
