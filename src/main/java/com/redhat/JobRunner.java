package com.redhat;

import io.quarkus.scheduler.Scheduled;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.transaction.Transactional;
import lombok.extern.java.Log;
import org.eclipse.microprofile.rest.client.RestClientBuilder;

import java.net.URI;

@ApplicationScoped
@Log
public class JobRunner {

    @Transactional
    @Scheduled(cron = "${job.schedule.time: 0 0 10 * * ?}")
    void schedule() {
        log.info("scheduled");
        RestClientBuilder builder = RestClientBuilder.newBuilder();
        MyRemoteService service = builder.baseUri(URI.create("https://api.crc.testing:6443")).build(MyRemoteService.class);
        log.info(service.getExtensionsById("myid"));
    }

}
