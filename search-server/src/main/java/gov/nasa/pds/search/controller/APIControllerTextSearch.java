package gov.nasa.pds.search.controller;

import java.io.PrintWriter;
import java.util.List;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.http.HttpEntity;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpRequestBase;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.util.EntityUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import gov.nasa.pds.nlp.NamedEntityRecognizer;
import gov.nasa.pds.nlp.Token;
import gov.nasa.pds.search.cfg.GeoConfiguration;
import gov.nasa.pds.search.cfg.SearchServerConfiguration;
import gov.nasa.pds.search.geo.GeoRequestBuilder;
import gov.nasa.pds.search.solr.IResponseWriter;
import gov.nasa.pds.search.solr.JsonResponseWriter;
import gov.nasa.pds.search.util.CloseUtils;
import gov.nasa.pds.search.util.HttpUtils;

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
        PrintWriter httpWriter = httpResp.getWriter();
        IResponseWriter respWriter = new JsonResponseWriter(httpWriter);

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
        
        try
        {
            String geoResp = getGeoResults(ssConfig.getGeoConfiguration());
            httpWriter.print(geoResp);
        }
        catch(Exception ex)
        {
            LOG.error(ex.toString());
            httpResp.setStatus(400);
            respWriter.error("Could not call Geo web service");
            return;
        }
    }

    
    private String getGeoResults(GeoConfiguration geoConf) throws Exception
    {
        HttpRequestBase req = createGeoRequest(geoConf.url);
        
        CloseableHttpClient httpClient = null;
        CloseableHttpResponse httpResponse = null;

        try
        {
            httpClient = HttpUtils.createHttpClient(geoConf.timeoutSec);
            httpResponse = httpClient.execute(req);

            int status = httpResponse.getStatusLine().getStatusCode();
            // Error
            if(status != 200)
            {
                throw new Exception(httpResponse.getStatusLine().getReasonPhrase());
            }
            
            HttpEntity entity = httpResponse.getEntity();
            if(entity != null) 
            {
                String result = EntityUtils.toString(entity);
                return result;
            }
        }
        finally
        {
            CloseUtils.safeClose(httpResponse);
            CloseUtils.safeClose(httpClient);
        }
        
        return "[]";
    }
    
    
    private HttpRequestBase createGeoRequest(String baseUrl) throws Exception
    {
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
