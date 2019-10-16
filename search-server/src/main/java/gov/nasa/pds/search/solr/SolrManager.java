package gov.nasa.pds.search.solr;

import org.apache.solr.client.solrj.SolrClient;
import org.apache.solr.client.solrj.impl.HttpSolrClient;

import gov.nasa.pds.search.cfg.SolrConfiguration;
import gov.nasa.pds.search.util.CloseUtils;

/**
 * A singleton to cache Solr connections.
 * @author karpenko
 */
public class SolrManager
{
    private static SolrManager instance;
    private SolrClient solrClient;
    
    /**
     * Private constructor. Use getInstance() instead.
     * @param cfg
     */
    private SolrManager(SolrConfiguration cfg)
    {
        solrClient = new HttpSolrClient.Builder(cfg.url).build();
    }
    
    /**
     * Returns an instance (singleton) of SolrManager.
     * @return
     */
    public static SolrManager getInstance()
    {
        return instance;
    }
    
    /**
     * Init SolrManager.
     * @param cfg
     */
    public static void init(SolrConfiguration cfg)
    {
        if(instance != null) throw new RuntimeException("Already initialized.");
        instance = new SolrManager(cfg);
    }
    
    /**
     * Close Solr connection(s) and free other resources. 
     */
    public static void destroy()
    {
        if(instance == null) return;
        CloseUtils.safeClose(instance.getSolrClient());
    }
    
    /**
     * Get an instance of a Solr client.
     * @return
     */
    public SolrClient getSolrClient()
    {
        return solrClient;
    }
}
