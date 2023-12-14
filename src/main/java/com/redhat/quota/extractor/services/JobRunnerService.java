package com.redhat.quota.extractor.services;

import com.redhat.quota.extractor.entities.commons.ExtractorEntity;
import io.quarkus.scheduler.Scheduled;
import io.smallrye.common.annotation.Blocking;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.transaction.Transactional;
import lombok.extern.slf4j.Slf4j;

@ApplicationScoped
@Slf4j
public class JobRunnerService {

    @Inject
    OcpExtractorService ocpExtractorService;

    @Scheduled(cron = "${extractor.job.schedule.time: 0 0 23 * * ?}")
    void schedule() {
        log.info("quota extractor wakeup");
        doJob();
        log.info("quota extractor sleep");
    }

    public void doJob() {
        log.info("full collection job start");
        prepareForJob();
        ocpExtractorService.executeExtraction();
        log.info("full collection job end");
    }

    @Blocking
    @Transactional
    void prepareForJob() {
        log.info("Clearing up database");
        ExtractorEntity.deleteAll();
        log.info("Database clear");
    }
}

