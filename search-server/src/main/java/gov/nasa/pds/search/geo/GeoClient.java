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
        if(query == null) return null;
        
        switch(query.getType())
        {
        case METADATA:
            return searchByMetadata((MetadataQuery)query);
        case LID:
            return searchByLid((LidQuery)query);
        }
        
        return null;
    }
    
    
    public Response searchByLid(LidQuery query) throws Exception
    {
        return null;
    }
    
    
    public Response searchByMetadata(MetadataQuery query) throws Exception
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
    
    
    private HttpRequestBase createGeoRequest(MetadataQuery query) throws Exception
    {
        MetadataRequestBuilder bld = new MetadataRequestBuilder(geoConf.url);
        bld.setTarget(query.targetId);
        bld.setMission(query.missionId);
        bld.setInstrument(query.instrumentId);
        bld.setProductType(query.productType);
        
        bld.setFeature(query.featureName);
        bld.setFeatureType(query.featureType);
        
        HttpGet req = bld.buildGet();        
        return req;
    }

}
