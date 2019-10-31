package gov.nasa.pds.search.geo;

import java.net.URI;

import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.utils.URIBuilder;


public class GeoRequestBuilder
{
    private URI baseUrl;
    private String outputFormat = "json";
    private String sortKey = "observationEndUtc";
    
    private String target;
    private String mission;
    private String instrument;
    
    private String productType;
    
    private String featureType;
    private String feature;
    
    private String offset;
    private String pageSize;
    
    
    public GeoRequestBuilder(String baseUrl) throws Exception
    {
        if(baseUrl == null) throw new IllegalArgumentException("Geo base URL is null");
        this.baseUrl = new URI(baseUrl);
    }
    
    public void setTarget(String target)
    {
        this.target = target;
    }
    
    public void setMission(String mission)
    {
        this.mission = mission;
    }
    
    public void setInstrument(String instrument)
    {
        this.instrument = instrument;
    }

    public void setProductType(String productType)
    {
        this.productType = productType;
    }

    public void setFeatureType(String featureType)
    {
        this.featureType = featureType;
    }

    public void setFeature(String feature)
    {
        this.feature = feature;
    }

        
    public HttpGet buildGet() throws Exception
    {
        URIBuilder bld = new URIBuilder(baseUrl);
        
        // Set new path
        String path = buildPath();
        bld.setPath(path);
        
        // Add parameters
        if(mission == null) 
        {
            throw new Exception("Mission is missing.");
        }
        else
        {
            bld.addParameter("mission", mission);
        }

        if(instrument != null)
        {
            bld.addParameter("instrument", instrument);
        }
        
        if(productType != null)
        {
            bld.addParameter("prodType", productType);
        }

        if(featureType != null)
        {
            bld.addParameter("featureType", featureType);
        }
        
        if(feature != null)
        {
            bld.addParameter("feature", feature);
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
    
    
    private String buildPath() throws Exception
    {
        // Normalize base path
        String basePath = baseUrl.getPath();
        StringBuilder pathBuilder = new StringBuilder(basePath);
        if(!basePath.endsWith("/"))
        {
            pathBuilder.append("/");
        }

        // Append target to base path
        if(target == null) throw new Exception("Target is missing.");
        pathBuilder.append(target);
        
        return pathBuilder.toString();
    }
}
