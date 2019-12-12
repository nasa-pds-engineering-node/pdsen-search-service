package gov.nasa.pds.search.geo;

import java.net.URI;

import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.utils.URIBuilder;

public class LidRequestBuilder
{
    private URI baseUrl;
    private String outputFormat = "json";

    
    public LidRequestBuilder(String baseUrl) throws Exception
    {
        if(baseUrl == null) throw new IllegalArgumentException("Geo base URL is null");
        this.baseUrl = new URI(baseUrl);
    }

    
    public HttpGet buildGet(LidQuery query) throws Exception
    {
        URIBuilder bld = new URIBuilder(baseUrl);
        
        // Set new path
        String path = buildPath(query);
        bld.setPath(path);
        
        // Add parameters
        bld.addParameter("lidvid", query.lid);
        
        if(outputFormat != null)
        {
            bld.addParameter("outputFormat", outputFormat);
        }
        
        URI uri = bld.build();
        HttpGet req = new HttpGet(uri);
        
        return req;
    }
    
    
    private String buildPath(LidQuery query) throws Exception
    {
        String basePath = baseUrl.getPath();
        StringBuilder pathBuilder = new StringBuilder(basePath);
        if(!basePath.endsWith("/"))
        {
            pathBuilder.append("/");
        }

        pathBuilder.append("products/lidvids/metadata");
        
        if(query.listFiles)
        {
            pathBuilder.append("/files");
        }
        
        return pathBuilder.toString();
    }

}
