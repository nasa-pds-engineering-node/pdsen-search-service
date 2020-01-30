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
import gov.nasa.pds.nlp.query.ContextQueryClass;
import gov.nasa.pds.nlp.query.ContextQueryClassifier;
import gov.nasa.pds.search.solr.SolrDocJsonWriter;
import gov.nasa.pds.search.solr.SolrManager;
import gov.nasa.pds.search.solr.query.InstrumentQueryBuilder;
import gov.nasa.pds.search.solr.query.InvestigationQueryBuilder;
import gov.nasa.pds.search.solr.query.SolrQueryUtils;
import gov.nasa.pds.search.solr.query.TargetQueryBuilder;
import gov.nasa.pds.search.util.RequestParameters;


@RestController
@RequestMapping(path = "/api/v1")
public class ContextSearchController
{
    private static final Logger LOG = LoggerFactory.getLogger(ContextSearchController.class);

    private static final String COLLECTION_INVESTIGATION = "ctx_invest";
    private static final String COLLECTION_INSTRUMENT = "ctx_instrument";
    private static final String COLLECTION_TARGET = "ctx_target";


    @Autowired
    private NamedEntityRecognizer ner;

    
    @GetMapping(path = "/search/ctx")
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
        ContextQueryClassifier queryClassifier = new ContextQueryClassifier();
        byte queryCategory = queryClassifier.classify(nerTokens);

        SolrDocumentList solrDocs = null;
        
        switch(queryCategory)
        {
        case ContextQueryClass.INVESTIGATION:
            solrDocs = runInvestigationQuery(nerTokens, reqParams);
            break;
        case ContextQueryClass.INSTRUMENT:
            solrDocs = runInstrumentQuery(nerTokens, reqParams);
            break;
        case ContextQueryClass.TARGET:
            solrDocs = runTargetQuery(nerTokens, reqParams);
            break;
        default:
            solrDocs = runUnknownQuery(nerTokens, reqParams);
            break;
        }
        
        // Invalid request
        if(solrDocs == null)
        {
            httpResp.setStatus(400);
            respWriter.error("Invalid query");
            return;
        }

        // Write documents
        respWriter.write(solrDocs);
    }
    
    
    private SolrDocumentList runTargetQuery(List<NerToken> tokens, 
            RequestParameters reqParams) throws Exception
    {
        // Build Solr query
        TargetQueryBuilder queryBuilder = new TargetQueryBuilder(tokens);
        SolrQuery query = queryBuilder.build();
        if(query == null) return null;

        // Set "start" and "rows"
        SolrQueryUtils.setPageInfo(query, reqParams);

        // Call Solr and get results
        SolrClient solrClient = SolrManager.getInstance().getSolrClient();
        QueryResponse resp = solrClient.query(COLLECTION_TARGET, query);
        SolrDocumentList docList = resp.getResults();

        return docList;
    }

    
    private SolrDocumentList runInstrumentQuery(List<NerToken> tokens, 
            RequestParameters reqParams) throws Exception
    {
        // Build Solr query
        InstrumentQueryBuilder queryBuilder = new InstrumentQueryBuilder(tokens);
        SolrQuery query = queryBuilder.build();
        if(query == null) return null;

        // Set "start" and "rows"
        SolrQueryUtils.setPageInfo(query, reqParams);

        // Call Solr and get results
        SolrClient solrClient = SolrManager.getInstance().getSolrClient();
        QueryResponse resp = solrClient.query(COLLECTION_INSTRUMENT, query);
        SolrDocumentList docList = resp.getResults();

        return docList;
    }

    
    private SolrDocumentList runInvestigationQuery(List<NerToken> tokens, 
            RequestParameters reqParams) throws Exception
    {
        // Build Solr query
        InvestigationQueryBuilder queryBuilder = new InvestigationQueryBuilder(tokens);
        SolrQuery query = queryBuilder.build();
        if(query == null) return null;

        // Set "start" and "rows"
        SolrQueryUtils.setPageInfo(query, reqParams);

        // Call Solr and get results
        SolrClient solrClient = SolrManager.getInstance().getSolrClient();
        QueryResponse resp = solrClient.query(COLLECTION_INVESTIGATION, query);
        SolrDocumentList docList = resp.getResults();

        return docList;
    }

    
    private SolrDocumentList runUnknownQuery(List<NerToken> tokens, 
            RequestParameters reqParams) throws Exception
    {
        return null;
    }
    
}
