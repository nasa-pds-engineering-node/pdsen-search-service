package tt;

import gov.nasa.pds.data.pds4.dao.InstrumentHostDAO;
import gov.nasa.pds.data.pds4.dao.InstrumentHostDAO_Solr;
import gov.nasa.pds.data.pds4.dao.SolrManager;
import gov.nasa.pds.data.pds4.ingest.ContextProductIngester;

public class TestContextIngester
{

    public static void main(String[] args) throws Exception
    {
        SolrManager.init("http://localhost:8983/solr");
        
        //String path = "/tmp/d2";
        String path = "/ws/data/pds4/context-pds4/instrument_host";
        
        ContextProductIngester ing = new ContextProductIngester();
        ing.crawl(path);
                
    }

    
    public static void testGetVersion() throws Exception
    {
        InstrumentHostDAO dao = new InstrumentHostDAO_Solr();
        float vid = dao.getVersion("urn:nasa:pds:context:instrument_host:spacecraft.a12a");
        System.out.println(vid);
    }
}
