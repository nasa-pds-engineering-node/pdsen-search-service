package tt;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.util.HashSet;
import java.util.Set;

import gov.nasa.pds.data.pds4.model.Instrument;
import gov.nasa.pds.data.pds4.model.InstrumentHost;
import gov.nasa.pds.data.pds4.model.Investigation;
import gov.nasa.pds.data.pds4.parser.InstrumentHostParser;
import gov.nasa.pds.data.pds4.parser.InstrumentParser;
import gov.nasa.pds.data.pds4.parser.InvestigationParser;
import gov.nasa.pds.data.util.xml.XmlDomCrawler;


public class ExportPds4Context
{

    public static void exportInstruments() throws Exception
    {
        String dir = "/ws/data/context/pds4/instrument";
        
        XmlDomCrawler crawler = new XmlDomCrawler(dir);
        InstrumentParser parser = new InstrumentParser();

        crawler.crawl((doc, path) -> 
        {
            Instrument inst = parser.parse(doc);
            
            if(inst.instrumentHostRef == null) return;
            System.out.println(inst.shortLid + "|" + inst.id + "|" + inst.name + "|" + inst.type + "|" + inst.instrumentHostRef);
        });
    }


    public static void exportMissions() throws Exception
    {
        String dir = "/ws/data/context/pds4/investigation";
        
        // Read file names of latest versions of context products
        Set<String> latestFiles = readLatestIndex(dir);
        
        XmlDomCrawler crawler = new XmlDomCrawler(dir);
        InvestigationParser parser = new InvestigationParser();

        crawler.crawl((doc, path) -> 
        {
            // Only process the latest versions
            String fileName = path.getFileName().toString();
            if(!latestFiles.contains(fileName)) return;
            
            Investigation inv = parser.parse(doc);
            if("Mission".equalsIgnoreCase(inv.type))
            {
                System.out.println(inv.shortLid + "|" + inv.name);
            }
        });
    }

    
    public static void exportInstrumentHosts() throws Exception
    {
        String dir = "/ws/data/context/pds4/instrument_host";
        
        // Read file names of latest versions of context products
        Set<String> latestFiles = readLatestIndex(dir);
        
        XmlDomCrawler crawler = new XmlDomCrawler(dir);
        InstrumentHostParser parser = new InstrumentHostParser();

        crawler.crawl((doc, path) -> 
        {
            // Only process the latest versions
            String fileName = path.getFileName().toString();
            if(!latestFiles.contains(fileName)) return;
            
            InstrumentHost ih = parser.parse(doc);
                        
            System.out.println(ih.name + "|" + ih.id);
        });
    }

    
    private static Set<String> readLatestIndex(String dir) throws Exception
    {
        File file = new File(dir, "latest.idx");
        if(!file.exists()) throw new RuntimeException("latest.xml index file doesn't exist in " + dir);
        
        BufferedReader rd = new BufferedReader(new FileReader(file));
        
        Set<String> set = new HashSet<>();
        
        String line;
        while((line = rd.readLine()) != null)
        {
            line = line.trim();
            if(line.isEmpty()) continue;
            
            set.add(line);
        }
        
        rd.close();
        
        return set;
    }    
    
    
    public static void main(String[] args) throws Exception
    {
        exportMissions();
        //exportInstrumentHosts();
        //exportInstruments();
    }
}
