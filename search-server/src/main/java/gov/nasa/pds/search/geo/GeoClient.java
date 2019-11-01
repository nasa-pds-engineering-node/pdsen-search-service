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
    private GeoConfiguration geoConf;
    
    
    public GeoClient(GeoConfiguration geoConf)
    {
        this.geoConf = geoConf;
    }
    

    public String search(BaseGeoQuery query) throws Exception
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
    
    
    public String searchByLid(LidQuery query) throws Exception
    {
        return null;
    }
    
    
    public String searchByMetadata(MetadataQuery query) throws Exception
    {
        HttpRequestBase req = createGeoRequest(geoConf.url);
        
        CloseableHttpClient httpClient = null;
        CloseableHttpResponse httpResponse = null;

        try
        {
            httpClient = HttpUtils.createHttpClient(geoConf.timeoutSec);
            httpResponse = httpClient.execute(req);

            int status = httpResponse.getStatusLine().getStatusCode();
            // Error
            if(status != 200)
            {
                throw new Exception(httpResponse.getStatusLine().getReasonPhrase());
            }
            
            HttpEntity entity = httpResponse.getEntity();
            if(entity != null) 
            {
                String result = EntityUtils.toString(entity);
                return result;
            }
        }
        finally
        {
            CloseUtils.safeClose(httpResponse);
            CloseUtils.safeClose(httpClient);
        }
        
        return "[]";
    }
    
    
    private HttpRequestBase createGeoRequest(String baseUrl) throws Exception
    {
        MetadataRequestBuilder bld = new MetadataRequestBuilder(baseUrl);
        bld.setTarget("mars");
        bld.setMission("mro");
        bld.setInstrument("crism");
        bld.setProductType("trdr");
        bld.setFeature("gale");
        bld.setFeatureType("crater");
        
        HttpGet req = bld.buildGet();        
        return req;
    }

}
