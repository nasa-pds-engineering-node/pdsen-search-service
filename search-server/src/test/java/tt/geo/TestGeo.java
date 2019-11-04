package tt.geo;

import java.io.File;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import gov.nasa.pds.nlp.MultiWordDictionary;
import gov.nasa.pds.nlp.NamedEntityRecognizer;
import gov.nasa.pds.search.cfg.ConfigurationLoader;
import gov.nasa.pds.search.cfg.SearchServerConfiguration;
import gov.nasa.pds.search.feature.FeatureRepo;
import gov.nasa.pds.search.geo.BaseGeoQuery;
import gov.nasa.pds.search.geo.GeoClient;
import gov.nasa.pds.search.geo.GeoQueryParser;


public class TestGeo
{
    private static final Logger LOG = LoggerFactory.getLogger(TestGeo.class);
    
    
    public static void main(String[] args)
    {
        try
        {
            FeatureRepo.init();
            
            //test1("MRO CRISM MRDRs over Gale Crater on Mars");
            //test1("LRO Moon");
            
            test1("urn:nasa:pds:mess_mla_calibrated:data_gdr:hdec_45n_500m");
        }
        catch(Exception ex)
        {
            ex.printStackTrace();
        }
    }

    
    private static void test1(String text) throws Exception
    {
        System.setProperty("pds.search.server.conf", "/ws/etc");
        SearchServerConfiguration ssCfg = ConfigurationLoader.load();
        
        // Init NER        
        MultiWordDictionary dic = new MultiWordDictionary();
        File file = new File(ssCfg.getConfigDirectory(), "ner.dic");
        dic.load(file);
        NamedEntityRecognizer ner = new NamedEntityRecognizer(dic);
        
        GeoClient geoClient = new GeoClient(ssCfg.getGeoConfiguration());
        GeoQueryParser queryParser = new GeoQueryParser(ner);
        BaseGeoQuery query = queryParser.parse(text);

        GeoClient.Response resp = geoClient.search(query);
        
        System.out.println(resp.status);
        System.out.println(resp.data);
    }

}
