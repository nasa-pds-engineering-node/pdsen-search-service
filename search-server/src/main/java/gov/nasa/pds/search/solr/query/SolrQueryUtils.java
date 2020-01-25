package gov.nasa.pds.search.solr.query;

import org.apache.solr.client.solrj.SolrQuery;

import gov.nasa.pds.search.util.RequestParameters;

public class SolrQueryUtils
{
    public static void setPageInfo(SolrQuery query, RequestParameters reqParams)
    {
        Integer rows = reqParams.getIntParameter("rows");
        if(rows != null)
        {
            query.setRows(rows);
        }
        
        Integer start = reqParams.getIntParameter("start");
        if(start != null)
        {
            query.setStart(start);
        }
    }
}
