package com.redhat.quota.extractor.persistance.entities;

import io.quarkus.hibernate.orm.panache.PanacheEntity;
import io.smallrye.common.annotation.Blocking;

import java.util.stream.Stream;

/**
 * superclass to enforce generic ActiveRecord Pattern operations
 */
public class ExtractorEntity extends PanacheEntity {

    /**
     * functional return for persisting a single entity
     * @return Void
     */
    @Blocking
    public static Void persistEntity(ExtractorEntity entity) {
        entity.persist();
        return null;
    }

    /**
     * functional return for persisting a collection of entities
     * @param entities a stream of ExtractorEntity
     * @return Void
     */
    @Blocking
    public static Void persistEntities(Stream<? extends ExtractorEntity> entities) {
        entities.forEach(ExtractorEntity::persistEntity);
        return null;
    }

}
