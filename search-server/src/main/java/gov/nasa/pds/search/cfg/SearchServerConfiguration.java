package gov.nasa.pds.search.cfg;

import java.io.File;

/**
 * PDS search server configuration.
 * @author karpenko
 */
public class SearchServerConfiguration
{
    private File configDir;
    private SolrConfiguration solrConfig;
    private FieldConfiguration fieldConfig;
    
    
    /**
     * Constructor
     */
    public SearchServerConfiguration(File configDir)
    {
        this.configDir = configDir;
        solrConfig = new SolrConfiguration();
        fieldConfig = new FieldConfiguration();
    }

    
    /**
     * Returns configuration folder.
     * @return
     */
    public File getConfigDirectory()
    {
        return configDir;
    }
    
    
    /**
     * Get Solr configuration 
     * @return
     */
    public SolrConfiguration getSolrConfiguration()
    {
        return solrConfig;
    }
    

    /**
     * Get field configuration 
     * @return
     */
    public FieldConfiguration getFieldConfiguration()
    {
        return fieldConfig;
    }

}
