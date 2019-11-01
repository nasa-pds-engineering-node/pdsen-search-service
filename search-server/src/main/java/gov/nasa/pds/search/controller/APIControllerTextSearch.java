package gov.nasa.pds.search.controller;

import java.io.PrintWriter;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import gov.nasa.pds.nlp.NamedEntityRecognizer;
import gov.nasa.pds.search.cfg.SearchServerConfiguration;
import gov.nasa.pds.search.geo.BaseGeoQuery;
import gov.nasa.pds.search.geo.GeoClient;
import gov.nasa.pds.search.geo.GeoQueryParser;
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
        
        try
        {
            GeoClient geoClient = new GeoClient(ssConfig.getGeoConfiguration());
            GeoQueryParser queryParser = new GeoQueryParser(ner);
            BaseGeoQuery query = queryParser.parse(text);
            if(query == null)
            {
                httpResp.setStatus(400);
                respWriter.error("Invalid query");
                return;
            }

            String geoResp = geoClient.search(query);
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

        
}
