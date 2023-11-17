package com.redhat.quota.extractor;

import com.redhat.quota.extractor.services.JobRunnerService;
import jakarta.inject.Inject;
import jakarta.ws.rs.POST;
import jakarta.ws.rs.Path;
import lombok.extern.java.Log;
import lombok.extern.slf4j.Slf4j;

@Path("/job")
@Slf4j
public class JobTestResource {

    @Inject
    JobRunnerService jobRunnerService;

    @POST
    public void doJob() {
        jobRunnerService.doJob();
    }

}
