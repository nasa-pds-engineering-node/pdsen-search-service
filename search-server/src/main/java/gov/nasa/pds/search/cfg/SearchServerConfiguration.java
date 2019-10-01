package gov.nasa.pds.search.cfg;

/**
 * PDS search server configuration.
 * @author karpenko
 */
public class SearchServerConfiguration
{
    private SolrConfiguration solrProps;
    
    /**
     * Constructor
     */
    public SearchServerConfiguration()
    {
        solrProps = new SolrConfiguration();
    }

    /**
     * Get Solr configuration 
     * @return
     */
    public SolrConfiguration getSolrConfiguration()
    {
        return solrProps;
    }
}
