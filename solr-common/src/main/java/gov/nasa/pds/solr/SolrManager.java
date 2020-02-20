package gov.nasa.pds.solr;

import java.io.Closeable;

import org.apache.solr.client.solrj.SolrClient;
import org.apache.solr.client.solrj.impl.HttpSolrClient;

import gov.nasa.pds.solr.cfg.SolrConfiguration;


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
        solrClient = new HttpSolrClient.Builder(cfg.getUrl()).build();
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
        safeClose(instance.getSolrClient());
    }
    
    /**
     * Get an instance of a Solr client.
     * @return
     */
    public SolrClient getSolrClient()
    {
        return solrClient;
    }
    

    private static void safeClose(Closeable cl)
    {
        if(cl == null) return;
        
        try
        {
            cl.close();
        }
        catch(Exception ex)
        {
            // Ignore
        }
    }
}
