package gov.nasa.pds.search.controller;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;


/**
 * SpringBoot controller to handle API Server status requests. 
 * @author karpenko
 */
@RestController
@RequestMapping(path = "/api/v1")
public class StatusController
{
    private static final int MEGABYTE = 1024 * 1000;

    @SuppressWarnings("unused")
    private static class StatusResponse
    {
        public String max_memory;
        public String total_memory;
        public String free_emory;
    }

    
    @GetMapping(path = "/status")
    public StatusResponse getStatus() throws Exception
    {
        StatusResponse resp = new StatusResponse();

        Runtime rt = Runtime.getRuntime();
        resp.max_memory = (rt.maxMemory() / MEGABYTE) + " MB";
        resp.total_memory = (rt.totalMemory() / MEGABYTE) + " MB"; 
        resp.free_emory = (rt.freeMemory() / MEGABYTE) + " MB";
        return resp;
    }
}
