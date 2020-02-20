package gov.nasa.pds.search.cfg;

import java.io.File;

import gov.nasa.pds.solr.cfg.SolrConfiguration;

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

    
    public void setSolrConfiguration(SolrConfiguration cfg)
    {
        this.solrConfig = cfg;
    }


    /**
     * Get field configuration 
     * @return
     */
    public FieldConfiguration getFieldConfiguration()
    {
        return fieldConfig;
    }

    
    public void setFieldConfiguration(FieldConfiguration cfg)
    {
        this.fieldConfig = cfg;
    }

}
