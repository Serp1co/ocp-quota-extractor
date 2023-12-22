package com.redhat.quota.extractor.services;

import com.redhat.quota.extractor.entities.commons.ExtractorEntity;
import com.redhat.quota.extractor.entities.crq.Annotations;
import com.redhat.quota.extractor.entities.crq.Labels;
import io.quarkus.scheduler.Scheduled;
import io.smallrye.common.annotation.Blocking;
import io.smallrye.mutiny.Multi;
import io.smallrye.mutiny.infrastructure.Infrastructure;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.transaction.Transactional;
import lombok.extern.slf4j.Slf4j;

@ApplicationScoped
@Slf4j
public class JobRunnerService {

    @Inject
    OcpExtractorService ocpExtractorService;

    @Inject
    ClientConfigService clientConfigService;

    @Scheduled(cron = "${extractor.job.schedule.time: 0 0 23 * * ?}")
    void schedule() {
        log.info("quota extractor wakeup");
        prepareForJob();
        doJob();
        log.info("quota extractor sleep");
    }

    public void doJob() {
        Multi.createFrom()
                .items(clientConfigService.getConfigsForClusters())
                .emitOn(Infrastructure.getDefaultWorkerPool())
                .subscribe()
                .with(ocpExtractorService::executeExtraction, Throwable::printStackTrace)
        ;
    }

    @Blocking
    @Transactional
    void prepareForJob() {
        log.info("Clearing up database");
        Annotations.deleteAll();
        Labels.deleteAll();
        ExtractorEntity.deleteAll();
        log.info("Database clear");
    }

}
