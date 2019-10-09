package gov.nasa.pds.search.controller;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.solr.client.solrj.SolrClient;
import org.apache.solr.client.solrj.SolrQuery;
import org.apache.solr.client.solrj.response.QueryResponse;
import org.apache.solr.common.SolrDocumentList;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import gov.nasa.pds.search.cfg.SearchServerConfiguration;
import gov.nasa.pds.search.cfg.SolrConfiguration;
import gov.nasa.pds.search.solr.JsonResponseWriter;
import gov.nasa.pds.search.solr.PdsApiQueryBuilder;
import gov.nasa.pds.search.solr.SolrManager;
import gov.nasa.pds.search.util.RequestParameters;


@RestController
@RequestMapping(path = "/api")
public class APIController
{
    @Autowired
    private SearchServerConfiguration ssConfig;
    
    
    @GetMapping(path = "/search/v1")
    public void getSearch(HttpServletRequest httpReq, HttpServletResponse httpResp) throws Exception
    {
        RequestParameters reqParams = new RequestParameters(httpReq.getParameterMap());
        
        // Use default JSON output format
        httpResp.setContentType("application/json");
        JsonResponseWriter respWriter = new JsonResponseWriter();
        
        // Build Solr query
        SolrConfiguration solrConfig = ssConfig.getSolrConfiguration();
        PdsApiQueryBuilder queryBuilder = new PdsApiQueryBuilder(solrConfig, reqParams);
        SolrQuery query = queryBuilder.build();
        
        // Invalid request
        if(query == null)
        {
            httpResp.setStatus(400);
            respWriter.error(httpResp.getOutputStream(), "Missing query parameter(s)");
            return;
        }
        
        // Call Solr and get results
        SolrClient solrClient = SolrManager.getInstance().getSolrClient();
        QueryResponse resp = solrClient.query(query);
        SolrDocumentList docList = resp.getResults();
        
        // Write documents
        respWriter.write(httpResp.getOutputStream(), docList);
    }
}
