package com.redhat.quota.extractor.entities.commons;

import io.quarkus.hibernate.orm.panache.PanacheEntity;
import io.smallrye.common.annotation.Blocking;
import jakarta.transaction.Transactional;

/**
 * superclass to enforce generic ActiveRecord Pattern operations
 */
public abstract class ExtractorEntity extends PanacheEntity {

    @Blocking
    @Transactional
    public static void persistEntityBlocking(ExtractorEntity e) {
        e.persist();
    }

}
