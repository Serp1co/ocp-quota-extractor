package com.redhat.quota.extractor.entities.commons;

import io.fabric8.openshift.client.OpenShiftClient;
import io.quarkus.hibernate.orm.panache.PanacheEntity;
import io.smallrye.common.annotation.Blocking;

import java.util.stream.Stream;

/**
 * superclass to enforce generic ActiveRecord Pattern operations
 */
public abstract class ExtractorEntity extends PanacheEntity {
}
