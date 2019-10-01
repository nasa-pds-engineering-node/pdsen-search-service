package gov.nasa.pds.search.cfg;

import java.io.FileReader;
import java.io.Reader;
import java.util.Properties;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import gov.nasa.pds.search.util.CloseUtils;

/**
 * Loads PDS search server configuration from a location provided in either 
 * JVM '-D' argument or environment variable.
 * @author karpenko
 */
public class ConfigurationLoader
{
    private static final String D_CONF = "pds.search.server.conf";
    private static final String ENV_CONF = "PDS_SEARCH_SERVER_CONF";
        
    private static final Logger LOG = LoggerFactory.getLogger(ConfigurationLoader.class);
    
    /**
     * Private constructor.
     */
    private ConfigurationLoader()
    {
    }
    
    /**
     * Loads PDS search server configuration
     * @return
     */
    public static SearchServerConfiguration load()
    {
        SearchServerConfiguration cfg = new SearchServerConfiguration();
        
        String path = getConfigFilePath();
        Properties props = readProperties(path);        
        setSolrProps(cfg, props);
        
        return cfg;
    }
    
    
    private static String getConfigFilePath()
    {
        String path = System.getProperty(D_CONF);
        if(path == null)
        {
            path = System.getenv(ENV_CONF);
        }
        
        if(path == null)
        {
            throw new RuntimeException("Missing JVM argument -D" + D_CONF + " or environment variable " + ENV_CONF);
        }

        return path;
    }
    
    
    private static Properties readProperties(String path)
    {
        LOG.info("Reading search server configuration from " + path);
        
        Properties props = new Properties();
        
        Reader rd = null;
        try
        {
            rd = new FileReader(path);
            props.load(rd);
        }
        catch(Exception ex)
        {
            throw new RuntimeException("Could not read properties from " + path + ": " + ex.getMessage()); 
        }
        finally
        {
            CloseUtils.safeClose(rd);
        }
        
        return props;
    }
    
    
    private static void setSolrProps(SearchServerConfiguration cfg, Properties props)
    {
        SolrConfiguration solrProps = cfg.getSolrConfiguration();
        
        solrProps.searchUrl = props.getProperty("solr.search.url");
        if(solrProps.searchUrl == null)
        {
            throw new RuntimeException("Missing property 'solr.search.url'");
        }
        
        solrProps.searchHandler = props.getProperty("solr.search.handler");
    }
}
