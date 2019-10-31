package gov.nasa.pds.search.controller;

import java.util.List;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpRequestBase;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import gov.nasa.pds.nlp.NamedEntityRecognizer;
import gov.nasa.pds.nlp.Token;
import gov.nasa.pds.search.cfg.SearchServerConfiguration;
import gov.nasa.pds.search.geo.GeoRequestBuilder;
import gov.nasa.pds.search.solr.IResponseWriter;
import gov.nasa.pds.search.solr.JsonResponseWriter;

@RestController
@RequestMapping(path = "/api")
public class APIControllerTextSearch
{
    private static final Logger LOG = LoggerFactory.getLogger(APIControllerTextSearch.class);
    
    @Autowired
    private SearchServerConfiguration ssConfig;
    @Autowired
    private NamedEntityRecognizer ner;

    
    @GetMapping(path = "/search/text/v1")
    public void getSearch(HttpServletRequest httpReq, HttpServletResponse httpResp) throws Exception
    {
        // Use default JSON output format
        httpResp.setContentType("application/json");
        IResponseWriter respWriter = new JsonResponseWriter(httpResp.getOutputStream());

        // Read query parameter
        String text = httpReq.getParameter("q");
        // Missing query parameter
        if(text == null)
        {
            httpResp.setStatus(400);
            respWriter.error("Missing query parameter");
            return;
        }
        
        List<Token> tokens = ner.parse(text);
        for(Token token: tokens)
        {
            System.out.println(token.text + " --> " + token.type);
        }
    }

    
    private HttpRequestBase createGeoRequest() throws Exception
    {
        String baseUrl = "https://pilot.rsl.wustl.edu/api/v1/search/products/metadata";
        
        GeoRequestBuilder bld = new GeoRequestBuilder(baseUrl);
        bld.setTarget("mars");
        bld.setMission("mro");
        bld.setInstrument("crism");
        bld.setProductType("trdr");
        bld.setFeature("gale");
        bld.setFeatureType("crater");
        
        HttpGet req = bld.buildGet();        
        return req;
    }
    
}
