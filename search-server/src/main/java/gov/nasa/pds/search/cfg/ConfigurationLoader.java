package gov.nasa.pds.search.cfg;

import java.io.File;


/**
 * Loads PDS search server configuration from a location provided in either 
 * JVM '-D' argument or environment variable.
 * @author karpenko
 */
public class ConfigurationLoader
{
    private static final String D_CONF = "pds.search.server.conf";
    private static final String ENV_CONF = "PDS_SEARCH_SERVER_CONF";
        
    
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
    public static SearchServerConfiguration load() throws Exception
    {
        String path = getConfigPath();
        
        File configDir = new File(path);
        if(!configDir.isDirectory())
        {
            throw new RuntimeException("Configuration path must be a directory: " + path);
        }
        
        SearchServerConfiguration cfg = new SearchServerConfiguration(configDir);
        
        SolrConfigurationLoader.load(cfg);
        FieldConfigurationLoader.load(cfg);
        
        return cfg;
    }
    
    
    private static String getConfigPath()
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

}
