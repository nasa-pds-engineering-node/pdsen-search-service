package gov.nasa.pds.search.solr;

import java.util.Arrays;
import java.util.List;
import java.util.Set;

import org.apache.solr.client.solrj.SolrQuery;

import gov.nasa.pds.search.solr.util.LuceneQueryBuilder;
import gov.nasa.pds.search.util.NameMapper;
import gov.nasa.pds.search.util.RequestParameters;
import gov.nasa.pds.solr.cfg.SolrCollectionConfiguration;


/**
 * Builds Solr query.
 * @author karpenko
 */
public class PdsApiQueryBuilder
{
    private static final String[] data_fields = { "Product_Data_Set_PDS3", "Product_Collection" };
    
    private NameMapper fieldNameMapper;
    private RequestParameters params;
    private Set<String> searchFields;
    private List<String> outFields;
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
    
    
    public void setOutputFields(List<String> fields)
    {
        this.outFields = fields;
    }
    

    public void setSearchFields(Set<String> fields)
    {
        this.searchFields = fields;
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
        if(outFields == null || outFields.size() == 0) return;
        
        for(String fieldName: outFields)
        {
            // Map public field name to internal Solr name
            String solrFieldName = (fieldNameMapper == null) ? fieldName : fieldNameMapper.findInternalByPublic(fieldName);
            query.addField(solrFieldName);
        }
    }
    
    
    private String buildQueryString()
    {
        LuceneQueryBuilder bld = new LuceneQueryBuilder();
        
        for(String paramName: params.getParameterNames())
        {
            // Map public parameter name to internal Solr field name
            String solrFieldName = (fieldNameMapper == null) ? paramName : fieldNameMapper.findInternalByPublic(paramName);

            if(searchFields.contains(solrFieldName))
            {
                bld.addField(true, solrFieldName, Arrays.asList(params.getParameterValues(paramName)));
            }
        }
        
        // TODO: This is a temporary fix.
        // TODO: Remove when we switch to a separate data collection.
        bld.addField(true, "objectType", Arrays.asList(data_fields));

        return bld.toString();
    }
}
