package gov.nasa.pds.search.controller;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import gov.nasa.pds.search.cfg.SearchServerConfiguration;

@RestController
@RequestMapping(path = "/api")
public class APIControllerTextSearch
{
    @Autowired
    private SearchServerConfiguration ssConfig;
    
    
    @GetMapping(path = "/search/text/v1")
    public void getSearch(HttpServletRequest httpReq, HttpServletResponse httpResp) throws Exception
    {
        System.out.println("test");
    }
}
