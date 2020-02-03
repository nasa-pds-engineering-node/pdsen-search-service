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
import gov.nasa.pds.search.solr.query.run.ContextQueryRunner;


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
        SearchContext ctx = new SearchContext(httpReq, httpResp);
        if(!ctx.validateAndContinue()) return;

        // Call NER
        PdsLexer lexer = new PdsLexer();
        List<String> lexTokens = lexer.parse(ctx.qParam);
        List<NerToken> nerTokens = ner.parse(lexTokens);
        ContextQueryClassifier queryClassifier = new ContextQueryClassifier();
        byte queryCategory = queryClassifier.classify(nerTokens);

        // Run Solr Query
        SolrDocumentList solrDocs = null;
        switch(queryCategory)
        {
        case ContextQueryClass.INVESTIGATION:
            solrDocs = ContextQueryRunner.runInvestigationQuery(nerTokens, ctx.reqParams);
            break;
        case ContextQueryClass.INSTRUMENT:
            solrDocs = ContextQueryRunner.runInstrumentQuery(nerTokens, ctx.reqParams);
            break;
        case ContextQueryClass.TARGET:
            solrDocs = ContextQueryRunner.runTargetQuery(nerTokens, ctx.reqParams);
            break;
        default:
            solrDocs = ContextQueryRunner.runUnknownQuery(nerTokens, ctx.reqParams);
            break;
        }
        
        if(!ctx.validateAndContinue(solrDocs)) return;

        // Write documents
        ctx.respWriter.write(solrDocs);
    }

    
    @GetMapping(path = "/search/ctx/invest")
    public void getInvestigation(HttpServletRequest httpReq, HttpServletResponse httpResp) throws Exception
    {
        SearchContext ctx = new SearchContext(httpReq, httpResp);
        if(!ctx.validateAndContinue()) return;
        
        // Call NER
        PdsLexer lexer = new PdsLexer();
        List<String> lexTokens = lexer.parse(ctx.qParam);
        List<NerToken> nerTokens = ner.parse(lexTokens);

        // Run Solr Query
        SolrDocumentList solrDocs = ContextQueryRunner.runInvestigationQuery(nerTokens, ctx.reqParams);
        if(!ctx.validateAndContinue(solrDocs)) return;

        // Write documents
        ctx.respWriter.write(solrDocs);
    }

    
    @GetMapping(path = "/search/ctx/instrument")
    public void getInstrument(HttpServletRequest httpReq, HttpServletResponse httpResp) throws Exception
    {
        SearchContext ctx = new SearchContext(httpReq, httpResp);
        if(!ctx.validateAndContinue()) return;
        
        // Call NER
        PdsLexer lexer = new PdsLexer();
        List<String> lexTokens = lexer.parse(ctx.qParam);
        List<NerToken> nerTokens = ner.parse(lexTokens);

        // Run Solr Query
        SolrDocumentList solrDocs = ContextQueryRunner.runInstrumentQuery(nerTokens, ctx.reqParams);
        if(!ctx.validateAndContinue(solrDocs)) return;

        // Write documents
        ctx.respWriter.write(solrDocs);
    }

    
    @GetMapping(path = "/search/ctx/target")
    public void getTarget(HttpServletRequest httpReq, HttpServletResponse httpResp) throws Exception
    {
        SearchContext ctx = new SearchContext(httpReq, httpResp);
        if(!ctx.validateAndContinue()) return;
        
        // Call NER
        PdsLexer lexer = new PdsLexer();
        List<String> lexTokens = lexer.parse(ctx.qParam);
        List<NerToken> nerTokens = ner.parse(lexTokens);

        // Run Solr Query
        SolrDocumentList solrDocs = ContextQueryRunner.runTargetQuery(nerTokens, ctx.reqParams);
        if(!ctx.validateAndContinue(solrDocs)) return;

        // Write documents
        ctx.respWriter.write(solrDocs);
    }

}
