package gov.nasa.pds.search.solr.query.run;

import java.util.List;

import org.apache.solr.client.solrj.SolrClient;
import org.apache.solr.client.solrj.SolrQuery;
import org.apache.solr.client.solrj.response.QueryResponse;

import gov.nasa.pds.nlp.ner.NerToken;
import gov.nasa.pds.search.solr.query.SolrQueryUtils;
import gov.nasa.pds.search.solr.query.bld.InstrumentQueryBuilder;
import gov.nasa.pds.search.solr.query.bld.InvestigationQueryBuilder;
import gov.nasa.pds.search.solr.query.bld.TargetQueryBuilder;
import gov.nasa.pds.search.util.RequestParameters;
import gov.nasa.pds.solr.SolrManager;


public class ContextQueryRunner
{
    private static final String COLLECTION_INVESTIGATION = "ctx_invest";
    private static final String COLLECTION_INSTRUMENT = "ctx_instrument";
    private static final String COLLECTION_TARGET = "ctx_target";

    
    public static QueryResponse runTargetQuery(List<NerToken> tokens, 
            RequestParameters reqParams) throws Exception
    {
        // Build Solr query
        TargetQueryBuilder queryBuilder = new TargetQueryBuilder(tokens);
        SolrQuery query = queryBuilder.build();
        if(query == null) return null;

        // Set "start" and "rows"
        SolrQueryUtils.setPageInfo(query, reqParams);
        // Set field list "fl"
        SolrQueryUtils.setFieldList(query, reqParams);

        // Call Solr and get results
        SolrClient solrClient = SolrManager.getInstance().getSolrClient();
        QueryResponse resp = solrClient.query(COLLECTION_TARGET, query);

        return resp;
    }

    
    public static QueryResponse runInstrumentQuery(List<NerToken> tokens, 
            RequestParameters reqParams) throws Exception
    {
        // Build Solr query
        InstrumentQueryBuilder queryBuilder = new InstrumentQueryBuilder(tokens);
        SolrQuery query = queryBuilder.build();
        if(query == null) return null;

        // Set "start" and "rows"
        SolrQueryUtils.setPageInfo(query, reqParams);
        // Set field list "fl"
        SolrQueryUtils.setFieldList(query, reqParams);

        // Call Solr and get results
        SolrClient solrClient = SolrManager.getInstance().getSolrClient();
        QueryResponse resp = solrClient.query(COLLECTION_INSTRUMENT, query);

        return resp;
    }

    
    public static QueryResponse runInvestigationQuery(List<NerToken> tokens, 
            RequestParameters reqParams) throws Exception
    {
        // Build Solr query
        InvestigationQueryBuilder queryBuilder = new InvestigationQueryBuilder(tokens);
        SolrQuery query = queryBuilder.build();
        if(query == null) return null;

        // Set "start" and "rows"
        SolrQueryUtils.setPageInfo(query, reqParams);
        // Set field list "fl"
        SolrQueryUtils.setFieldList(query, reqParams);
        
        // Call Solr and get results
        SolrClient solrClient = SolrManager.getInstance().getSolrClient();
        QueryResponse resp = solrClient.query(COLLECTION_INVESTIGATION, query);

        return resp;
    }

    
    public static QueryResponse runUnknownQuery(List<NerToken> tokens, 
            RequestParameters reqParams) throws Exception
    {
        return null;
    }

}
