package gov.nasa.pds.search.cfg;

/**
 * A singleton which holds PDS search server configuration. 
 * @author karpenko
 */
public class ConfigurationManager
{
    private static ConfigurationManager instance;
    private SearchServerConfiguration ssCfg;
    
    /**
     * Private constructor
     */
    private ConfigurationManager()
    {
        ssCfg = ConfigurationLoader.load();
    }
    
    /**
     * Get the singleton.
     * @return
     */
    public static ConfigurationManager getInstance()
    {
        return instance;
    }
    
    /**
     * Load new configuration.
     */
    public static void init()
    {
        instance = new ConfigurationManager();
    }
    
    /**
     * Get PDS search server configuration. 
     * @return
     */
    public SearchServerConfiguration getSearchServerConfiguration()
    {
        return ssCfg; 
    }
}
