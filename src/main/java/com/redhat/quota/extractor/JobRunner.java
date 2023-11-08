package com.redhat.quota.extractor;

import com.redhat.quota.extractor.services.collector.OcpCollectorService;
import io.quarkus.scheduler.Scheduled;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.transaction.Transactional;
import lombok.extern.java.Log;

@ApplicationScoped
@Log
public class JobRunner {

    @Inject
    OcpCollectorService ocpCollectorService;

    @Transactional
    @Scheduled(cron = "${extractor.job.schedule.time: 0 0 23 * * ?}")
    void schedule() {
        doJob();
    }

    public void doJob() {
        log.info("job start");
        ocpCollectorService.doFullCollection();
        log.info("job end");
    }

}

