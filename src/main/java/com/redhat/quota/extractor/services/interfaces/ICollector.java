package com.redhat.quota.extractor.services.interfaces;

import com.redhat.quota.extractor.models.Namespaces;
import com.redhat.quota.extractor.models.Nodes;

import java.util.List;

public interface ICollector {
    List<Namespaces> collectNamespaces();

    List<Nodes> collectNodes();

}
