package tt;

import java.io.BufferedWriter;
import java.io.FileWriter;

import gov.nasa.pds.data.pds3.model.Pds3Instrument;
import gov.nasa.pds.data.pds3.model.Pds3InstrumentHost;
import gov.nasa.pds.data.pds3.model.Pds3Target;
import gov.nasa.pds.data.pds3.parser.Pds3InstrumentHostParser;
import gov.nasa.pds.data.pds3.parser.Pds3InstrumentParser;
import gov.nasa.pds.data.pds3.parser.Pds3TargetParser;
import gov.nasa.pds.data.util.xml.XmlDomCrawler;


public class ExtractNames
{
    public static void main(String[] args) throws Exception
    {
        extractPds3Target();
    }

    
    private static void extractPds3Target() throws Exception
    {
        XmlDomCrawler crawler = new XmlDomCrawler("/ws/data/context/pds3/target");
        Pds3TargetParser parser = new Pds3TargetParser();
        
        final BufferedWriter wr = new BufferedWriter(new FileWriter("/tmp/targets.pds3"));
        
        crawler.crawl((doc, path) -> 
        {
            Pds3Target tgt = parser.parse(doc);
            wr.write(tgt.shortLid + "|" + tgt.name + "|" + tgt.type + "|" + tgt.primaryBodyName + "\n");
        });
        
        wr.close();
    }

    
    private static void extractPds3Spacecraft() throws Exception
    {
        XmlDomCrawler crawler = new XmlDomCrawler("/ws/data/pds4/context-pds3/instrument_host");
        Pds3InstrumentHostParser parser = new Pds3InstrumentHostParser();
        
        crawler.crawl((doc, path) -> 
        {
            Pds3InstrumentHost ih = parser.parse(doc);
            if("Spacecraft".equalsIgnoreCase(ih.type))
            {
                System.out.print(ih.shortLid + "|" + ih.id + "|" + ih.name + "|");
                print(ih.investigationRef);
                System.out.println();
            }
        });
    }

    
    private static void extractPds3Instrument() throws Exception
    {
        XmlDomCrawler crawler = new XmlDomCrawler("/ws/data/pds4/context-pds3/instrument");
        Pds3InstrumentParser parser = new Pds3InstrumentParser();
        
        crawler.crawl((doc, path) -> 
        {
            Pds3Instrument inst = parser.parse(doc);
            if(!"SPICE KERNELS".equalsIgnoreCase(inst.name))
            {
                System.out.println(inst.shortLid + "|" + inst.id + "|" + inst.name + "|" 
                        + inst.instrumentHostRef + "|" + inst.type);
            }
        });
    }

    
    private static void print(String[] arr)
    {
        if(arr == null) System.out.print("null");

        for(int i = 0; i < arr.length; i++)
        {
            if(i != 0) System.out.print(",");
            System.out.print(arr[i]);
        }
        
    }
}
