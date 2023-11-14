package com.redhat.quota.extractor;

import com.redhat.quota.extractor.collectors.ICollector;
import com.redhat.quota.extractor.collectors.NamespacesCollector;
import com.redhat.quota.extractor.collectors.NodesCollector;
import com.redhat.quota.extractor.services.OcpExtractorService;
import io.quarkus.scheduler.Scheduled;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.transaction.Transactional;
import lombok.extern.java.Log;

import java.util.List;

@ApplicationScoped
@Log
public class JobRunner {

    @Inject
    OcpExtractorService ocpExtractorService;

    final List<ICollector> collectors = List.of(
            new NamespacesCollector(),
            new NodesCollector()
    );

    @Transactional
    @Scheduled(cron = "${extractor.job.schedule.time: 0 0 23 * * ?}")
    void schedule() {
        log.info("quota extractor wakeup");
        doJob();
        log.info("quota extractor sleeping");
    }

    public void doJob() {
        log.info("full collection job start");
        ocpExtractorService.executeExtraction(this.collectors);
        log.info("full collection job done");
    }

}

