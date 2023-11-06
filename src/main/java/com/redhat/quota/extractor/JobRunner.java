package com.redhat.quota.extractor;

import com.redhat.quota.extractor.services.impl.QuotaCollectorService;
import io.quarkus.scheduler.Scheduled;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.transaction.Transactional;
import lombok.extern.java.Log;

@ApplicationScoped
@Log
public class JobRunner {

    @Inject
    QuotaCollectorService quotaCollectorService;

    @Transactional
    @Scheduled(cron = "${job.schedule.time: 0 0 10 * * ?}")
    void schedule() {
        doJob();
    }

    public void doJob() {
        quotaCollectorService.collectNamespaces().forEach(s -> log.info(s.toString()));
        quotaCollectorService.collectNodes().forEach(s -> log.info(s.toString()));
    }


}

