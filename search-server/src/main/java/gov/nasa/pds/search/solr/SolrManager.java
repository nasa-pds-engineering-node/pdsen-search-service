package gov.nasa.pds.search.solr;

import org.apache.solr.client.solrj.SolrClient;
import org.apache.solr.client.solrj.impl.HttpSolrClient;

import gov.nasa.pds.search.cfg.SolrConfiguration;
import gov.nasa.pds.search.util.CloseUtils;

public class SolrManager
{
    private static SolrManager instance;
    private SolrClient solrClient;
    
    private SolrManager(SolrConfiguration cfg)
    {
        solrClient = new HttpSolrClient.Builder(cfg.searchUrl).build();
    }
    
    public static SolrManager getInstance()
    {
        return instance;
    }
    
    public static void init(SolrConfiguration cfg)
    {
        if(instance != null) throw new RuntimeException("Already initialized.");
        instance = new SolrManager(cfg);
    }
    
    public static void destroy()
    {
        if(instance == null) return;
        CloseUtils.safeClose(instance.getSolrClient());
    }
    
    public SolrClient getSolrClient()
    {
        return solrClient;
    }
}
