package com.redhat.quota.extractor;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.redhat.quota.extractor.exception.ApplicationException;
import jakarta.inject.Inject;
import jakarta.ws.rs.POST;
import jakarta.ws.rs.Path;
import lombok.extern.java.Log;

@Path("/job")
@Log
public class JobTestResource {

    @Inject
    JobRunner jobRunner;

    @POST
    public void doJob() throws ApplicationException, JsonProcessingException {
        jobRunner.doJob();
    }

}
