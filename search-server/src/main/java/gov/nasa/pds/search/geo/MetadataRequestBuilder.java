package gov.nasa.pds.search.geo;

import java.net.URI;

import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.utils.URIBuilder;


public class MetadataRequestBuilder
{
    private URI baseUrl;
    private String outputFormat = "json";
    private String sortKey = "observationEndUtc";
    
    private String offset;
    private String pageSize;
    
    
    public MetadataRequestBuilder(String baseUrl) throws Exception
    {
        if(baseUrl == null) throw new IllegalArgumentException("Geo base URL is null");
        this.baseUrl = new URI(baseUrl);
    }
    
        
    public HttpGet buildGet(MetadataQuery query) throws Exception
    {
        URIBuilder bld = new URIBuilder(baseUrl);
        
        // Set new path
        String path = buildPath(query);
        bld.setPath(path);
        
        // Add parameters
        if(query.missionId == null) 
        {
            throw new Exception("Mission is missing.");
        }
        else
        {
            bld.addParameter("mission", query.missionId);
        }

        if(query.instrumentId != null)
        {
            bld.addParameter("instrument", query.instrumentId);
        }
        
        if(query.productType != null)
        {
            bld.addParameter("prodType", query.productType);
        }

        if(query.featureType != null)
        {
            bld.addParameter("featureType", query.featureType);
        }
        
        if(query.featureName != null)
        {
            bld.addParameter("feature", query.featureName);
        }

        if(sortKey != null)
        {
            bld.addParameter("sortKey", sortKey);
        }
        
        if(pageSize != null)
        {
            bld.addParameter("pageSize", pageSize);
        }

        if(offset != null)
        {
            bld.addParameter("offset", offset);
        }

        if(outputFormat != null)
        {
            bld.addParameter("outputFormat", outputFormat);
        }
        
        URI uri = bld.build();
        HttpGet req = new HttpGet(uri);
        
        return req;
    }
    
    
    private String buildPath(MetadataQuery query) throws Exception
    {
        String basePath = baseUrl.getPath();
        StringBuilder pathBuilder = new StringBuilder(basePath);
        if(!basePath.endsWith("/"))
        {
            pathBuilder.append("/");
        }

        pathBuilder.append("products/metadata/");
        
        // Append target to base path
        if(query.targetId == null) throw new Exception("Target is missing.");
        pathBuilder.append(query.targetId);

        return pathBuilder.toString();
    }
}
