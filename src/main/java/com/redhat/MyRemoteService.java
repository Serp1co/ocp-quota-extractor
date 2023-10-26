package com.redhat;

import org.eclipse.microprofile.rest.client.annotation.ClientHeaderParam;

import jakarta.ws.rs.GET;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.QueryParam;
import org.eclipse.microprofile.rest.client.annotation.RegisterClientHeaders;

import java.util.List;

//@ClientHeaderParam(name = "Authorization", value = "{lookupAuth}")
@RegisterClientHeaders
public interface MyRemoteService {

    /*default String lookupAuth(String headerName) {
        if ("SomeHeader".equals(headerName)) {
            return "InvokedCount ";
        }
        throw new UnsupportedOperationException("unknown header name");
    }*/

    @GET
    @Path("/extensions")
    String getExtensionsById(@QueryParam("id") String id);

    class Extension {
        public String id;
        public String name;
        public String shortName;
        public List<String> keywords;
    }
}
