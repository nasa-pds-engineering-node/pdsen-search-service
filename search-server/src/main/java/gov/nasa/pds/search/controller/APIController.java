package gov.nasa.pds.search.controller;

import java.util.ArrayList;
import java.util.List;
import java.util.StringTokenizer;

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
        SolrConfiguration solrConfig = ssConfig.getSolrConfiguration();

        // Use default JSON output format
        httpResp.setContentType("application/json");
        List<String> fields = getFields(reqParams, solrConfig);
        JsonResponseWriter respWriter = new JsonResponseWriter(httpResp.getOutputStream(), fields);
        
        // Build Solr query
        PdsApiQueryBuilder queryBuilder = new PdsApiQueryBuilder(reqParams, fields, solrConfig);
        SolrQuery query = queryBuilder.build();
        
        // Invalid request
        if(query == null)
        {
            httpResp.setStatus(400);
            respWriter.error("Missing query parameter(s)");
            return;
        }
        
        // Call Solr and get results
        SolrClient solrClient = SolrManager.getInstance().getSolrClient();
        QueryResponse resp = solrClient.query(query);
        SolrDocumentList docList = resp.getResults();
        
        // Write documents
        respWriter.write(docList);
    }
    
    
    // Get a list of fields to return.
    private List<String> getFields(RequestParameters reqParams, SolrConfiguration solrConfig)
    {
        String pFields = reqParams.getParameter("fields");
        if(pFields != null && !pFields.isEmpty())
        {
            List<String> fields = new ArrayList<>();
            StringTokenizer tkz = new StringTokenizer(pFields, ",; ");
            while(tkz.hasMoreTokens())
            {
                String field = tkz.nextToken(); 
                if(!field.isEmpty()) 
                {
                    fields.add(field);
                }
            }
            
            if(!fields.isEmpty()) return fields;
        }
        
        return solrConfig.defaultFields;
    }
}
