package gov.nasa.pds.search.solr.query.run;

import java.util.List;

import org.apache.solr.client.solrj.SolrClient;
import org.apache.solr.client.solrj.SolrQuery;
import org.apache.solr.client.solrj.response.QueryResponse;

import gov.nasa.pds.nlp.ner.NerToken;
import gov.nasa.pds.search.solr.query.SolrQueryUtils;
import gov.nasa.pds.search.solr.query.bld.DataQueryBuilder;
import gov.nasa.pds.search.solr.util.SolrManager;
import gov.nasa.pds.search.util.RequestParameters;


public class DataQueryRunner
{
    private static final String COLLECTION_DATA = "data";
    private static final String DEFAULT_FACET_FIELD = "science_facets";
    
    
    public static QueryResponse runDataQuery(List<NerToken> tokens, 
            RequestParameters reqParams) throws Exception
    {
        // Build Solr query
        DataQueryBuilder queryBuilder = new DataQueryBuilder(tokens);
        FacetUtils.addFacetFilters(queryBuilder, reqParams);        
        SolrQuery query = queryBuilder.build();
        if(query == null) return null;

        // Set "fl", "start", "rows"
        setCommonSolrFields(query, reqParams);
        FacetUtils.setFacets(query, reqParams, DEFAULT_FACET_FIELD);
        
        // Call Solr and get results
        SolrClient solrClient = SolrManager.getInstance().getSolrClient();
        QueryResponse resp = solrClient.query(COLLECTION_DATA, query);

        return resp;
    }

    
    private static void setCommonSolrFields(SolrQuery query, RequestParameters reqParams)
    {
        SolrQueryUtils.setPageInfo(query, reqParams);
        SolrQueryUtils.setFieldList(query, reqParams);
    }
}
