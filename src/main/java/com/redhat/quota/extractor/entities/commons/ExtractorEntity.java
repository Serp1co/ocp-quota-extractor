package com.redhat.quota.extractor.entities.commons;

import io.quarkus.hibernate.orm.panache.PanacheEntity;
import io.smallrye.common.annotation.Blocking;
import jakarta.transaction.Transactional;

import java.util.List;
import java.util.stream.Stream;

/**
 * superclass to enforce generic ActiveRecord Pattern operations
 */
public abstract class ExtractorEntity extends PanacheEntity {

    @Transactional
    @Blocking
    public static void persistAll(List<? extends ExtractorEntity> entityStream) {
        entityStream.forEach(
                e -> e.persist()
        );
    }

}
