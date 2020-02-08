package gov.nasa.pds.search.solr.query.run;

import org.apache.solr.client.solrj.SolrQuery;

import gov.nasa.pds.search.solr.query.bld.DataQueryBuilder;
import gov.nasa.pds.search.util.FieldMap;
import gov.nasa.pds.search.util.RequestParameters;

public class FacetUtils
{
    public static void addFacetFilters(DataQueryBuilder queryBuilder, RequestParameters reqParams)
    {
        FieldMap fmap = new FieldMap();
        
        for(String fieldName: reqParams.getParameterNames())
        {
            if(fieldName.startsWith("ff."))
            {
                String value = reqParams.getParameter(fieldName);
                fmap.addValue(fieldName.substring(3), value);
            }
        }
        
        if(!fmap.isEmpty())
        {
            queryBuilder.addFields(fmap);
        }
    }
    
    
    public static void setFacets(SolrQuery query, RequestParameters reqParams, String defaultFacetField)
    {
        // Facet on/off
        String pFacet = reqParams.getParameter("facet");
        if(!"true".equalsIgnoreCase(pFacet) && !"on".equalsIgnoreCase(pFacet)) 
        {
            return;
        }
        else
        {
            query.set("facet", true);
        }
        
        // Facet fields
        String[] facetFields = reqParams.getParameterValues("facet.field");
        if(facetFields == null || facetFields.length == 0)
        {
            query.set("facet.field", defaultFacetField);
        }
        else
        {
            query.set("facet.field", facetFields);
        }
        
        // Configuration
        query.set("facet.mincount", reqParams.getIntParameter("facet.mincount", 1));
        query.set("facet.limit", reqParams.getIntParameter("facet.limit", 15));
    }

}
