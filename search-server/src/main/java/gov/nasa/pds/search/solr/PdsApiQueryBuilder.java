package gov.nasa.pds.search.solr;

import org.apache.solr.client.solrj.SolrQuery;

import gov.nasa.pds.search.cfg.SolrConfiguration;
import gov.nasa.pds.search.util.RequestParameters;


public class PdsApiQueryBuilder
{
    private SolrConfiguration solrConfig;
    private RequestParameters params;
    
    
    public PdsApiQueryBuilder(SolrConfiguration solrConfig, RequestParameters params)
    {
        this.solrConfig = solrConfig;
        this.params = params;
    }

    
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
        String[] fields = { "identifier", "title", "mission_name" };
        
        query.setFields(fields);
    }
    
    
    private String buildQueryString()
    {
        SolrQueryStringBuilder bld = new SolrQueryStringBuilder();
        
        for(String paramName: params.getParameterNames())
        {
            switch(paramName)
            {
            case "target":
                bld.addField("target_name", params.getParameterValues(paramName));
                break;
            case "investigation":
                bld.addField("investigation_name", params.getParameterValues(paramName));
                break;
            case "instrument":
                bld.addField("instrument_name", params.getParameterValues(paramName));
                break;
            }
        }
        
        return bld.toString();
    }
}
