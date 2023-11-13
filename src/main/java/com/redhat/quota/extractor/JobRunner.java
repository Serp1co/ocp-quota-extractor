package com.redhat.quota.extractor;

import com.redhat.quota.extractor.services.OcpExtractorService;
import io.quarkus.scheduler.Scheduled;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.transaction.Transactional;
import lombok.extern.java.Log;

@ApplicationScoped
@Log
public class JobRunner {

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
        ocpExtractorService.executeFullExtraction();
        log.info("full collection job done");
    }

}

