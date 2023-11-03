package com.redhat;

import io.quarkus.scheduler.Scheduled;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.transaction.Transactional;
import lombok.extern.java.Log;

import java.net.URI;

@ApplicationScoped
@Log
public class JobRunner {

    @Transactional
    @Scheduled(cron = "${job.schedule.time: 0 0 10 * * ?}")
    void schedule() {
    }

}
