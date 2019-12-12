package gov.nasa.pds.search.geo;


import org.apache.http.HttpEntity;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpRequestBase;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.util.EntityUtils;

import gov.nasa.pds.search.cfg.GeoConfiguration;
import gov.nasa.pds.search.util.CloseUtils;
import gov.nasa.pds.search.util.HttpUtils;


public class GeoClient
{
    public static class Response
    {
        public int status;
        public String data;
    }
    
    
    private GeoConfiguration geoConf;
    
    
    public GeoClient(GeoConfiguration geoConf)
    {
        this.geoConf = geoConf;
    }
    

    public Response search(BaseGeoQuery query) throws Exception
    {
        HttpRequestBase req = createGeoRequest(query);
        
        CloseableHttpClient httpClient = null;
        CloseableHttpResponse httpResponse = null;

        try
        {
            httpClient = HttpUtils.createHttpClient(geoConf.timeoutSec);
            httpResponse = httpClient.execute(req);

            Response resp = new Response();
            resp.status = httpResponse.getStatusLine().getStatusCode();
            
            if(resp.status == 200)
            {
                HttpEntity entity = httpResponse.getEntity();
                if(entity == null)
                {
                    resp.data = "[]";
                }
                else
                {
                    resp.data = EntityUtils.toString(entity);
                }
            }
            else
            {
                resp.data = httpResponse.getStatusLine().getReasonPhrase(); 
            }
            
            return resp;
        }
        finally
        {
            CloseUtils.safeClose(httpResponse);
            CloseUtils.safeClose(httpClient);
        }
    }
    

    private HttpRequestBase createGeoRequest(BaseGeoQuery query) throws Exception
    {
        HttpGet req = null;
        
        if(query.getType() == BaseGeoQuery.Type.LID)
        {
            LidQuery lq = (LidQuery)query;            
            LidRequestBuilder bld = new LidRequestBuilder(geoConf.url);
            req = bld.buildGet(lq);
        }
        else if(query.getType() == BaseGeoQuery.Type.METADATA)
        {
            MetadataQuery mq = (MetadataQuery)query;            
            MetadataRequestBuilder bld = new MetadataRequestBuilder(geoConf.url);
            req = bld.buildGet(mq);
        }
        
        return req;
    }
    
}
