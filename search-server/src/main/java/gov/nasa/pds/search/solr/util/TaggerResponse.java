package gov.nasa.pds.search.solr.util;

import org.apache.solr.client.solrj.response.SolrResponseBase;
import org.apache.solr.common.SolrDocumentList;
import org.apache.solr.common.util.NamedList;

public class TaggerResponse extends SolrResponseBase
{
    private static final long serialVersionUID = 1L;

    private SolrDocumentList results;
    
    public TaggerResponse()
    {
    }

    @Override
    public void setResponse(NamedList<Object> response)
    {
        super.setResponse(response);

        for(int i = 0; i < response.size(); i++)
        {
            String name = response.getName(i);

            if("response".equals(name))
            {
                results = (SolrDocumentList)response.getVal(i);
            }
        }
    }

    
    public SolrDocumentList getResults()
    {
        return results;
    }

}
