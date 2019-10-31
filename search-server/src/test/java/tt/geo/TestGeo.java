package tt.geo;

import org.apache.http.HttpEntity;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpRequestBase;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.util.EntityUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import gov.nasa.pds.search.geo.GeoRequestBuilder;
import gov.nasa.pds.search.util.CloseUtils;
import gov.nasa.pds.search.util.HttpUtils;


public class TestGeo
{
    private static final Logger LOG = LoggerFactory.getLogger(TestGeo.class);
    
    
    public static void main(String[] args) throws Exception
    {
        HttpRequestBase req = createGeoRequest();
        
        CloseableHttpClient httpClient = null;
        CloseableHttpResponse httpResponse = null;
        
        try
        {
            httpClient = HttpUtils.createHttpClient(5);
            httpResponse = httpClient.execute(req);

            int status = httpResponse.getStatusLine().getStatusCode();
            // Error
            if(status != 200)
            {
                LOG.error(httpResponse.getStatusLine().getReasonPhrase());
                return;
            }
            
            HttpEntity entity = httpResponse.getEntity();
            if(entity != null) 
            {
                String result = EntityUtils.toString(entity);
                System.out.println(result);
            }
        }
        catch(Exception ex)
        {
            LOG.error(ex.toString());
        }
        finally
        {
            CloseUtils.safeClose(httpResponse);
            CloseUtils.safeClose(httpClient);
        }
    }

    
    private static HttpRequestBase createGeoRequest() throws Exception
    {
        String baseUrl = "https://pilot.rsl.wustl.edu/api/v1/search/products/metadata";
        
        GeoRequestBuilder bld = new GeoRequestBuilder(baseUrl);
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
