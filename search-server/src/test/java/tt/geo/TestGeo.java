package tt.geo;

import java.net.URI;

import org.apache.http.HttpEntity;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.util.EntityUtils;

import gov.nasa.pds.search.geo.GeoRequestBuilder;


public class TestGeo
{

    public static void main(String[] args) throws Exception
    {
        String baseUrl = "https://pilot.rsl.wustl.edu/api/v1/search/products/metadata";
        
        GeoRequestBuilder bld = new GeoRequestBuilder(baseUrl);
        bld.setTarget("mars");
        bld.setMission("mro");
        bld.setInstrument("crism");
        bld.setProductType("trdr");
        bld.setFeature("gale");
        bld.setFeatureType("crater");
        
        URI uri = bld.buildGet();
        System.out.println(uri);
        
        HttpGet req = new HttpGet(uri);
        
        try
        (
            CloseableHttpClient httpClient = HttpClients.createDefault();
            CloseableHttpResponse response = httpClient.execute(req)
        ) 
        {
            System.out.println(response.getStatusLine().getStatusCode());
            System.out.println(response.getStatusLine().getReasonPhrase());
            
            HttpEntity entity = response.getEntity();
            if(entity != null) 
            {
                String result = EntityUtils.toString(entity);
                System.out.println(result);
            }
        }
    }

}
