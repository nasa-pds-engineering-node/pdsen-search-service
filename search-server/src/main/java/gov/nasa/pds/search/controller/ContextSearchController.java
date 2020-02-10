package gov.nasa.pds.search.controller;

import java.util.List;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.solr.client.solrj.response.QueryResponse;
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
    @Autowired
    private ContextQueryClassifier queryClassifier;
    
    
    @GetMapping(path = "/search/context")
    public void getContext(HttpServletRequest httpReq, HttpServletResponse httpResp) throws Exception
    {
        SearchContext ctx = new SearchContext(httpReq, httpResp);
        if(!ctx.validateAndContinue()) return;

        // Call NER
        PdsLexer lexer = new PdsLexer();
        List<String> lexTokens = lexer.parse(ctx.qParam);
        List<NerToken> nerTokens = ner.parse(lexTokens);
        byte queryCategory = queryClassifier.classify(lexTokens, nerTokens);

        // Run Solr Query
        QueryResponse qResp = null;
        switch(queryCategory)
        {
        case ContextQueryClass.INVESTIGATION:
            qResp = ContextQueryRunner.runInvestigationQuery(nerTokens, ctx.reqParams);
            break;
        case ContextQueryClass.INSTRUMENT:
            qResp = ContextQueryRunner.runInstrumentQuery(nerTokens, ctx.reqParams);
            break;
        case ContextQueryClass.TARGET:
            qResp = ContextQueryRunner.runTargetQuery(nerTokens, ctx.reqParams);
            break;
        default:
            qResp = ContextQueryRunner.runUnknownQuery(nerTokens, ctx.reqParams);
            break;
        }
        
        if(!ctx.validateAndContinue(qResp)) return;

        // Write documents
        ctx.respWriter.write(qResp);
    }

    
    @GetMapping(path = "/search/context/investigation")
    public void getInvestigation(HttpServletRequest httpReq, HttpServletResponse httpResp) throws Exception
    {
        SearchContext ctx = new SearchContext(httpReq, httpResp);
        if(!ctx.validateAndContinue()) return;
        
        // Call NER
        PdsLexer lexer = new PdsLexer();
        List<String> lexTokens = lexer.parse(ctx.qParam);
        List<NerToken> nerTokens = ner.parse(lexTokens);

        // Run Solr Query
        QueryResponse qResp = ContextQueryRunner.runInvestigationQuery(nerTokens, ctx.reqParams);
        if(!ctx.validateAndContinue(qResp)) return;

        // Write documents
        ctx.respWriter.write(qResp);
    }

    
    @GetMapping(path = "/search/context/instrument")
    public void getInstrument(HttpServletRequest httpReq, HttpServletResponse httpResp) throws Exception
    {
        SearchContext ctx = new SearchContext(httpReq, httpResp);
        if(!ctx.validateAndContinue()) return;
        
        // Call NER
        PdsLexer lexer = new PdsLexer();
        List<String> lexTokens = lexer.parse(ctx.qParam);
        List<NerToken> nerTokens = ner.parse(lexTokens);

        // Run Solr Query
        QueryResponse qResp = ContextQueryRunner.runInstrumentQuery(nerTokens, ctx.reqParams);
        if(!ctx.validateAndContinue(qResp)) return;

        // Write documents
        ctx.respWriter.write(qResp);
    }

    
    @GetMapping(path = "/search/context/target")
    public void getTarget(HttpServletRequest httpReq, HttpServletResponse httpResp) throws Exception
    {
        SearchContext ctx = new SearchContext(httpReq, httpResp);
        if(!ctx.validateAndContinue()) return;
        
        // Call NER
        PdsLexer lexer = new PdsLexer();
        List<String> lexTokens = lexer.parse(ctx.qParam);
        List<NerToken> nerTokens = ner.parse(lexTokens);

        // Run Solr Query
        QueryResponse qResp = ContextQueryRunner.runTargetQuery(nerTokens, ctx.reqParams);
        if(!ctx.validateAndContinue(qResp)) return;

        // Write documents
        ctx.respWriter.write(qResp);
    }

}
