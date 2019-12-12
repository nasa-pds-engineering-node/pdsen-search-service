package gov.nasa.pds.search.controller;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.solr.client.solrj.SolrClient;
import org.apache.solr.client.solrj.SolrQuery;
import org.apache.solr.client.solrj.response.QueryResponse;
import org.apache.solr.common.SolrDocumentList;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import gov.nasa.pds.search.cfg.SearchServerConfiguration;
import gov.nasa.pds.search.cfg.SolrCollectionConfiguration;
import gov.nasa.pds.search.solr.IResponseWriter;
import gov.nasa.pds.search.solr.JsonResponseWriter;
import gov.nasa.pds.search.solr.SolrManager;
import gov.nasa.pds.search.util.RequestParameters;


@RestController
@RequestMapping(path = "/api")
public class NameController
{
    private static final Logger LOG = LoggerFactory.getLogger(NameController.class);
    
    @Autowired
    private SearchServerConfiguration ssConfig;

    
    @GetMapping(path = "/name/v1")
    public void getName(HttpServletRequest httpReq, HttpServletResponse httpResp) throws Exception
    {
        RequestParameters reqParams = new RequestParameters(httpReq.getParameterMap());

        httpResp.setContentType("application/json");
        IResponseWriter respWriter = new JsonResponseWriter(httpResp.getWriter());

        String qParam = reqParams.getParameter("q");
        
        // Invalid request
        if(qParam == null || qParam.isEmpty())
        {
            httpResp.setStatus(400);
            respWriter.error("Missing query parameter(s)");
            return;
        }
        
        SolrCollectionConfiguration solrConfig = ssConfig.getSolrConfiguration().getCollectionConfiguration("names");
        if(solrConfig == null)
        {
            httpResp.setStatus(500);
            respWriter.error("Internal server error. See logs for details.");
            LOG.error("Could not get configuration for Solr collection 'names'");
            return;
        }
        
        //TODO: Parse and process the text
        SolrQuery query = new SolrQuery("name:" + qParam);
        if(solrConfig.requestHandler != null)
        {
            query.setRequestHandler(solrConfig.requestHandler);
        }
        
        // Call Solr and get results
        SolrClient solrClient = SolrManager.getInstance().getSolrClient();
        QueryResponse resp = solrClient.query(solrConfig.collectionName, query);
        SolrDocumentList docList = resp.getResults();
        
        // Write documents
        respWriter.write(docList);
    }
}
