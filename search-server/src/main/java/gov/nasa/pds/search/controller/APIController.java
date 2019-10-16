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

import gov.nasa.pds.search.cfg.FieldConfiguration;
import gov.nasa.pds.search.cfg.SearchServerConfiguration;
import gov.nasa.pds.search.cfg.SolrCollectionConfiguration;
import gov.nasa.pds.search.solr.IResponseWriter;
import gov.nasa.pds.search.solr.JsonResponseWriter;
import gov.nasa.pds.search.solr.PdsApiQueryBuilder;
import gov.nasa.pds.search.solr.SolrManager;
import gov.nasa.pds.search.util.NameMapper;
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
        IResponseWriter respWriter = new JsonResponseWriter(httpResp.getOutputStream());
        
        // TODO: Validate
        SolrCollectionConfiguration solrConfig = ssConfig.getSolrConfiguration().getCollectionConfiguration("data");
        FieldConfiguration fieldConfig = ssConfig.getFieldConfiguration();
        
        // Decide which fields to return
        List<String> fields = getFields(reqParams, fieldConfig);
        respWriter.setFields(fields);
        respWriter.setNameMapper(fieldConfig.nameMapper);
        
        // Build Solr query
        PdsApiQueryBuilder queryBuilder = new PdsApiQueryBuilder(reqParams, solrConfig);
        queryBuilder.setFieldNameMapper(fieldConfig.nameMapper);
        queryBuilder.setFields(fields);        
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
        QueryResponse resp = solrClient.query(solrConfig.collectionName, query);
        SolrDocumentList docList = resp.getResults();
        
        // Write documents
        respWriter.write(docList);
    }
    
    
    // Get a list of fields to return.
    private List<String> getFields(RequestParameters reqParams, FieldConfiguration fieldConfig)
    {
        String pFields = reqParams.getParameter("fields");
        if(pFields != null && !pFields.isEmpty())
        {
            NameMapper nameMapper = fieldConfig.nameMapper;
            
            List<String> fields = new ArrayList<>();
            StringTokenizer tkz = new StringTokenizer(pFields, ",; ");
            while(tkz.hasMoreTokens())
            {
                String field = tkz.nextToken(); 
                if(!field.isEmpty()) 
                {
                    String solrFieldName = (nameMapper == null) ? field : nameMapper.findInternalByPublic(field);
                    fields.add(solrFieldName);
                }
            }
            
            if(!fields.isEmpty()) return fields;
        }
        
        return fieldConfig.defaultFields;
    }
}
