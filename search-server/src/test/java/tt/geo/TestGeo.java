package tt.geo;

import java.io.File;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import gov.nasa.pds.nlp.MultiWordDictionary;
import gov.nasa.pds.nlp.NamedEntityRecognizer;
import gov.nasa.pds.search.cfg.ConfigurationLoader;
import gov.nasa.pds.search.cfg.SearchServerConfiguration;
import gov.nasa.pds.search.geo.GeoClient;


public class TestGeo
{
    private static final Logger LOG = LoggerFactory.getLogger(TestGeo.class);
    
    
    public static void main(String[] args)
    {
        try
        {
            test1();
        }
        catch(Exception ex)
        {
            ex.printStackTrace();
        }
    }

    
    private static void test1() throws Exception
    {
        System.setProperty("pds.search.server.conf", "/ws/etc");
        SearchServerConfiguration ssCfg = ConfigurationLoader.load();
        
        // Init NER        
        MultiWordDictionary dic = new MultiWordDictionary();
        File file = new File(ssCfg.getConfigDirectory(), "ner.dic");
        dic.load(file);
        NamedEntityRecognizer ner = new NamedEntityRecognizer(dic);
        
        GeoClient geoClient = new GeoClient(ssCfg.getGeoConfiguration());
        String resp = geoClient.search(null);
        
        System.out.println(resp);
    }

}
