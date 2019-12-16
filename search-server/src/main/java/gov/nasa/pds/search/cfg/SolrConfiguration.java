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
    
    
    public SolrCollectionConfiguration getCollectionConfiguration(String id)
    {
        return collectionMap.get(id);
    }
    
    
    public void setUrl(String url)
    {
        this.url = url;
    }

    
    public void addCollectionConfiguration(String id, SolrCollectionConfiguration cfg)
    {
        collectionMap.put(id, cfg);
    }
}
