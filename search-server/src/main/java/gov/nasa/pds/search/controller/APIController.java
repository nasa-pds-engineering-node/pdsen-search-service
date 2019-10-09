package gov.nasa.pds.search.controller;

import javax.servlet.http.HttpServletRequest;

import org.apache.solr.client.solrj.SolrClient;
import org.apache.solr.client.solrj.SolrQuery;
import org.apache.solr.client.solrj.response.QueryResponse;
import org.apache.solr.common.SolrDocument;
import org.apache.solr.common.SolrDocumentList;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import gov.nasa.pds.search.cfg.SearchServerConfiguration;
import gov.nasa.pds.search.cfg.SolrConfiguration;
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
    public String getSearch(HttpServletRequest req) throws Exception
    {
        RequestParameters reqParams = new RequestParameters(req.getParameterMap());
        SolrConfiguration solrConfig = ssConfig.getSolrConfiguration();
        
        PdsApiQueryBuilder queryBuilder = new PdsApiQueryBuilder(solrConfig, reqParams);
        SolrQuery query = queryBuilder.build();
        
        if(query == null)
        {
            // TODO: Fix
            return "Missing query parameters.";
        }
        
        SolrClient solrClient = SolrManager.getInstance().getSolrClient();
        QueryResponse resp = solrClient.query(query);
        SolrDocumentList res = resp.getResults();
        
        // Process search results
        for(SolrDocument doc: res)
        {
            System.out.println(doc.getFirstValue("identifier"));
            System.out.println(doc.getFirstValue("title"));
            System.out.println();
        }
        
        return "{}";
    }
}
