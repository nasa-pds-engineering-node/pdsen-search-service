package gov.nasa.pds.search.solr;

import java.util.List;

import org.apache.solr.client.solrj.SolrQuery;

import gov.nasa.pds.search.cfg.SolrCollectionConfiguration;
import gov.nasa.pds.search.util.NameMapper;
import gov.nasa.pds.search.util.RequestParameters;


/**
 * Builds Solr query.
 * @author karpenko
 */
public class PdsApiQueryBuilder
{
    private NameMapper fieldNameMapper;
    private RequestParameters params;
    private List<String> fields;
    private SolrCollectionConfiguration solrConfig;
    
    
    /**
     * Constructor.
     * @param params Request parameters
     */
    public PdsApiQueryBuilder(RequestParameters params, SolrCollectionConfiguration solrConfig)
    {
        this.params = params;
        this.solrConfig = solrConfig;
    }


    public void setFieldNameMapper(NameMapper mapper)
    {
        this.fieldNameMapper = mapper;
    }
    
    
    public void setFields(List<String> fields)
    {
        this.fields = fields;
    }
    
    
    /**
     * Build Solr query.
     * @return solr query object.
     */
    public SolrQuery build()
    {
        // Get / build Solr query string
        String queryString = getQueryString();
        if(queryString == null)
        {
            return null;
        }
        
        SolrQuery query = new SolrQuery(queryString);
        
        // Set request handler
        if(solrConfig != null && solrConfig.requestHandler != null)
        {
            query.setRequestHandler(solrConfig.requestHandler);
        }
        
        // Add fields
        addFields(query);
        
        // TODO: Read from parameters
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
    
    
    private void addFields(SolrQuery query)
    {
        if(fields == null || fields.size() == 0) return;
        
        for(String fieldName: fields)
        {
            // Map public field name to internal Solr name
            String solrFieldName = (fieldNameMapper == null) ? fieldName : fieldNameMapper.findInternalByPublic(fieldName);
            query.addField(solrFieldName);
        }
    }
    
    
    private String buildQueryString()
    {
        SolrQueryStringBuilder bld = new SolrQueryStringBuilder();
        
        for(String paramName: params.getParameterNames())
        {
            // Map public parameter name to internal Solr field name
            String solrFieldName = (fieldNameMapper == null) ? paramName : fieldNameMapper.findInternalByPublic(paramName);
            
            switch(solrFieldName)
            {
            case "investigation_name":
            case "instrument_name":
            case "instrument_host_name":
            case "target_name":
                bld.addField(solrFieldName, params.getParameterValues(paramName));
                break;
            }
        }
        
        return bld.toString();
    }
}
