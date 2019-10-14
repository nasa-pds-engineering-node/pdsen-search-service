package gov.nasa.pds.search.solr;

import java.util.List;

import org.apache.solr.client.solrj.SolrQuery;

import gov.nasa.pds.search.cfg.SolrConfiguration;
import gov.nasa.pds.search.util.RequestParameters;


/**
 * Builds Solr query.
 * @author karpenko
 */
public class PdsApiQueryBuilder
{
    private SolrConfiguration solrConfig;
    private RequestParameters params;
    List<String> returnFields;
    
    
    /**
     * Constructor.
     * @param params Request parameters
     * @param returnFields A list of fields to return
     * @param solrConfig Solr configuration
     */
    public PdsApiQueryBuilder(RequestParameters params, List<String> returnFields, SolrConfiguration solrConfig)
    {
        if(returnFields == null || returnFields.isEmpty()) 
        {
            throw new IllegalArgumentException("Return fields could not be null or empty");
        }
        
        this.solrConfig = solrConfig;
        this.params = params;
        this.returnFields = returnFields;
    }

    /**
     * Build Solr query.
     * @return solr query object.
     */
    public SolrQuery build()
    {
        String queryString = getQueryString();
        if(queryString == null)
        {
            return null;
        }
        
        SolrQuery query = new SolrQuery(queryString);
        setRequestHandler(query);
        setFields(query);
        query.setRows(10);
        
        return query;
    }
    
    
    private String getQueryString()
    {
        String queryString = params.getParameter("q");
        
        if(queryString == null)
        {
            queryString = buildQueryString();
        }
        
        return queryString;
    }
    
    
    private void setRequestHandler(SolrQuery query)
    {
        if(solrConfig.searchHandler != null)
        {
            query.setRequestHandler(solrConfig.searchHandler);
        }
    }
    
    
    private void setFields(SolrQuery query)
    {
        String[] fieldArray = new String[returnFields.size()];
        returnFields.toArray(fieldArray);
        
        query.setFields(fieldArray);
    }
    
    
    private String buildQueryString()
    {
        SolrQueryStringBuilder bld = new SolrQueryStringBuilder();
        
        for(String paramName: params.getParameterNames())
        {
            switch(paramName)
            {
            case "investigation_name":
            case "instrument_name":
            case "instrument_host_name":
            case "target_name":
                bld.addField(paramName, params.getParameterValues(paramName));
                break;
            }
        }
        
        return bld.toString();
    }
}
