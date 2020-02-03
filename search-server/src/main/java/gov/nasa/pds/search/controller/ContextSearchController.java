package gov.nasa.pds.search.controller;

import java.util.List;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

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
import gov.nasa.pds.search.solr.query.run.ContextQueryRunner;
import gov.nasa.pds.search.util.RequestParameters;


@RestController
@RequestMapping(path = "/api/v1")
public class ContextSearchController
{
    private static final Logger LOG = LoggerFactory.getLogger(ContextSearchController.class);

    @Autowired
    private NamedEntityRecognizer ner;

    
    @GetMapping(path = "/search/ctx")
    public void getContext(HttpServletRequest httpReq, HttpServletResponse httpResp) throws Exception
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
            solrDocs = ContextQueryRunner.runInvestigationQuery(nerTokens, reqParams);
            break;
        case ContextQueryClass.INSTRUMENT:
            solrDocs = ContextQueryRunner.runInstrumentQuery(nerTokens, reqParams);
            break;
        case ContextQueryClass.TARGET:
            solrDocs = ContextQueryRunner.runTargetQuery(nerTokens, reqParams);
            break;
        default:
            solrDocs = ContextQueryRunner.runUnknownQuery(nerTokens, reqParams);
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

}
