package gov.nasa.pds.solr.util;

import org.apache.solr.client.solrj.SolrClient;
import org.apache.solr.client.solrj.SolrRequest;
import org.apache.solr.client.solrj.request.RequestWriter;
import org.apache.solr.client.solrj.request.RequestWriter.StringPayloadContentWriter;
import org.apache.solr.common.params.SolrParams;


public class TaggerRequest extends SolrRequest<TaggerResponse>
{
    private static final long serialVersionUID = 1L;

    private String body;
    private SolrParams params;
    
    
    public TaggerRequest(String body)
    {
        super(METHOD.POST, "/tag");
        this.body = body;
    }

    
    @Override
    public SolrParams getParams()
    {
        return params;
    }
    
    
    public void setParams(SolrParams params)
    {
        this.params = params;
    }

    
    @Override
    protected TaggerResponse createResponse(SolrClient client)
    {
        return new TaggerResponse();
    }

    
    @Override
    public RequestWriter.ContentWriter getContentWriter(String expectedType)
    {
        return new StringPayloadContentWriter(body, "text/plain");
    }
    
}
