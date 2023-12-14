package com.redhat.quota.extractor.collectors;

import com.redhat.quota.extractor.entities.commons.ExtractorEntity;
import io.smallrye.common.annotation.Blocking;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.transaction.Transactional;
import lombok.extern.slf4j.Slf4j;

import java.util.List;

@Slf4j
@ApplicationScoped
public abstract class ACollector {

    @Blocking
    @Transactional
    void persist(List<? extends ExtractorEntity> entityStream) {
        log.debug("persisting entities={}", entityStream);
        ExtractorEntity.persistAll(entityStream);
    }

}
