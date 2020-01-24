package gov.nasa.pds.search.controller;

import java.util.List;

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

import gov.nasa.pds.nlp.lex.PdsLexer;
import gov.nasa.pds.nlp.ner.NamedEntityRecognizer;
import gov.nasa.pds.nlp.ner.NerToken;
import gov.nasa.pds.search.cfg.SearchServerConfiguration;
import gov.nasa.pds.search.solr.SolrDocJsonWriter;
import gov.nasa.pds.search.solr.SolrManager;
import gov.nasa.pds.search.solr.query.DataQueryBuilder;
import gov.nasa.pds.search.util.RequestParameters;


@RestController
@RequestMapping(path = "/api/v1")
public class DataSearchController
{
    private static final Logger LOG = LoggerFactory.getLogger(DataSearchController.class);
    
    @Autowired
    private SearchServerConfiguration ssConfig;
    @Autowired
    private NamedEntityRecognizer ner;

    
    @GetMapping(path = "/search/data")
    public void getName(HttpServletRequest httpReq, HttpServletResponse httpResp) throws Exception
    {
        RequestParameters reqParams = new RequestParameters(httpReq.getParameterMap());

        httpResp.setContentType("application/json");
        SolrDocJsonWriter respWriter = new SolrDocJsonWriter(httpResp.getWriter());

        String qParam = reqParams.getParameter("q");
        
        // Validate request
        if(qParam == null || qParam.isEmpty())
        {
            httpResp.setStatus(400);
            respWriter.error("Missing query parameter (q=...)");
            return;
        }
        
        // Call NER
        PdsLexer lexer = new PdsLexer();
        List<String> lexTokens = lexer.parse(qParam);
        List<NerToken> nerTokens = ner.parse(lexTokens);

        // Build Solr query
        DataQueryBuilder queryBuilder = new DataQueryBuilder(nerTokens);
        SolrQuery query = queryBuilder.build();
        
        // Invalid request
        if(query == null)
        {
            httpResp.setStatus(400);
            respWriter.error("Missing query parameter(s)");
            return;
        }

        // Set "start" and "rows"
        setPageInfo(query, reqParams);
        
        // Call Solr and get results
        SolrClient solrClient = SolrManager.getInstance().getSolrClient();
        QueryResponse resp = solrClient.query(getSolrCollectionName(), query);
        SolrDocumentList docList = resp.getResults();
        
        // Write documents
        respWriter.write(docList);
    }
    
    
    private void setPageInfo(SolrQuery query, RequestParameters reqParams)
    {
        Integer rows = reqParams.getIntParameter("rows");
        if(rows != null)
        {
            query.setRows(rows);
        }
        
        Integer start = reqParams.getIntParameter("start");
        if(start != null)
        {
            query.setStart(start);
        }
    }

    
    private String getSolrCollectionName()
    {
        return "data";
    }
}
