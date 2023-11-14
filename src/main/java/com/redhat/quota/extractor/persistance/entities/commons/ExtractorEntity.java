package com.redhat.quota.extractor.persistance.entities.commons;

import io.quarkus.hibernate.orm.panache.PanacheEntity;
import io.smallrye.common.annotation.Blocking;
import lombok.extern.java.Log;

import java.util.stream.Stream;

/**
 * superclass to enforce generic ActiveRecord Pattern operations
 */
public class ExtractorEntity extends PanacheEntity {

    /**
     * functional return for persisting a collection of entities
     * @param entities a stream of ExtractorEntity
     * @return Void
     */
    @Blocking
    public static Void persistEntities(Stream<? extends ExtractorEntity> entities) {
        ExtractorEntity.persist(entities);
        return null;
    }

}
