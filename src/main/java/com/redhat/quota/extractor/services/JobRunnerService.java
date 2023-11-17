package com.redhat.quota.extractor.services;

import io.quarkus.scheduler.Scheduled;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.transaction.Transactional;
import lombok.extern.slf4j.Slf4j;

@ApplicationScoped
@Slf4j
public class JobRunnerService {

    @Inject
    OcpExtractorService ocpExtractorService;

    @Transactional
    @Scheduled(cron = "${extractor.job.schedule.time: 0 0 23 * * ?}")
    void schedule() {
        log.info("quota extractor wakeup");
        doJob();
        log.info("quota extractor sleeping");
    }

    public void doJob() {
        log.info("full collection job start");
        ocpExtractorService.executeExtraction();
        log.info("full collection job done");
    }

}

