package tt;

import gov.nasa.pds.data.pds3.model.Pds3Instrument;
import gov.nasa.pds.data.pds3.parser.Pds3InstrumentParser;
import gov.nasa.pds.data.util.xml.XmlDomCrawler;


public class ExportPds3Instruments
{

    public static void crawlMissions() throws Exception
    {
        String dir = "/ws/data/context/pds3/instrument";
        
        XmlDomCrawler crawler = new XmlDomCrawler(dir);
        Pds3InstrumentParser parser = new Pds3InstrumentParser();

        crawler.crawl((doc, path) -> 
        {
            Pds3Instrument inst = parser.parse(doc);
            
            if("SPICE".equals(inst.id)) return;
            System.out.println(inst.shortLid + "|" + inst.id + "|" + inst.name + "|" + inst.type + "|" + inst.instrumentHostRef);
        });
    }

    
    public static void main(String[] args) throws Exception
    {
        //crawlInstHosts();
        crawlMissions();
    }
}
