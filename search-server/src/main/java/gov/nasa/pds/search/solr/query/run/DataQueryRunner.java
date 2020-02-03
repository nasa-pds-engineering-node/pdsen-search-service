package gov.nasa.pds.search.solr.query.run;

import java.util.List;

import org.apache.solr.client.solrj.SolrClient;
import org.apache.solr.client.solrj.SolrQuery;
import org.apache.solr.client.solrj.response.QueryResponse;
import org.apache.solr.common.SolrDocumentList;

import gov.nasa.pds.nlp.ner.NerToken;
import gov.nasa.pds.search.solr.query.SolrQueryUtils;
import gov.nasa.pds.search.solr.query.bld.DataQueryBuilder;
import gov.nasa.pds.search.solr.util.SolrManager;
import gov.nasa.pds.search.util.RequestParameters;


public class DataQueryRunner
{
    private static final String COLLECTION_DATA = "data";

    
    public static SolrDocumentList runDataQuery(List<NerToken> tokens, 
            RequestParameters reqParams) throws Exception
    {
        // Build Solr query
        DataQueryBuilder queryBuilder = new DataQueryBuilder(tokens);
        SolrQuery query = queryBuilder.build();
        if(query == null) return null;

        // Set "start" and "rows"
        SolrQueryUtils.setPageInfo(query, reqParams);
        // Set field list "fl"
        SolrQueryUtils.setFields(query, reqParams);

        // Call Solr and get results
        SolrClient solrClient = SolrManager.getInstance().getSolrClient();
        QueryResponse resp = solrClient.query(COLLECTION_DATA, query);
        SolrDocumentList docList = resp.getResults();

        return docList;
    }

}
