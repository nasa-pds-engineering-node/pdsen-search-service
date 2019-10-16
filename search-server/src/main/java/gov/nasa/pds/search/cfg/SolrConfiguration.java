package gov.nasa.pds.search.cfg;

import java.util.HashMap;
import java.util.Map;

/**
 * Solr configuration parameters.
 * @author karpenko
 */
public class SolrConfiguration
{
    private String url;
    private Map<String, SolrCollectionConfiguration> collectionMap;
    
    
    public SolrConfiguration()
    {
        collectionMap = new HashMap<>();
    }

    
    public String getUrl()
    {
        return url;
    }
    
    
    public SolrCollectionConfiguration getCollectionConfiguration(String name)
    {
        return collectionMap.get(name);
    }
    
    
    public void setUrl(String url)
    {
        this.url = url;
    }

    
    public void addCollectionConfiguration(String name, SolrCollectionConfiguration cfg)
    {
        collectionMap.put(name, cfg);
    }
}
