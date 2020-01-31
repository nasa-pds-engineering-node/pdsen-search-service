package tt;

import gov.nasa.pds.search.cfg.ConfigurationLoader;
import gov.nasa.pds.search.cfg.FieldConfiguration;
import gov.nasa.pds.search.cfg.SearchServerConfiguration;
import gov.nasa.pds.search.cfg.SolrCollectionConfiguration;
import gov.nasa.pds.search.cfg.SolrConfiguration;


public class TestConfigurationLoader
{
    public static void main(String[] args) throws Exception
    {
        System.setProperty("pds.search.server.conf", "src/main/conf");
        
        SearchServerConfiguration cfg = ConfigurationLoader.load();
        
        SolrConfiguration solrCfg = cfg.getSolrConfiguration();        
        System.out.println(solrCfg.getUrl());
        
        SolrCollectionConfiguration cconf = solrCfg.getCollectionConfiguration("data");
        System.out.println(cconf.collectionName);
        System.out.println(cconf.requestHandler);
        
        FieldConfiguration fieldCfg = cfg.getFieldConfiguration();
        for(String str: fieldCfg.defaultFields)
        {
            System.out.println(str);
        }
        
        System.out.println("-------------------------------");
        System.out.println(fieldCfg.nameMapper.findInternalByPublic("investigation"));
        
        System.out.println("-------------------------------\n");
    }
}
