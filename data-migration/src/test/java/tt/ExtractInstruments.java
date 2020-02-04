package tt;

import java.io.FileWriter;
import java.io.Writer;
import java.util.Map;
import java.util.Set;
import java.util.TreeSet;

import org.apache.commons.lang.WordUtils;

import gov.nasa.pds.data.pds4.model.Instrument;
import gov.nasa.pds.data.pds4.parser.InstrumentParser;
import gov.nasa.pds.data.util.MapUtils;
import gov.nasa.pds.data.util.xml.SolrDocUtils;
import gov.nasa.pds.data.util.xml.XmlDomCrawler;


public class ExtractInstruments
{

    public static void extractInstrumentIds() throws Exception
    {
        String dir = "/ws/data/context/pds4/instrument";
        
        XmlDomCrawler crawler = new XmlDomCrawler(dir);
        InstrumentParser parser = new InstrumentParser();

        Set<String> ids = new TreeSet<>();
        
        crawler.crawl((doc, path) -> 
        {
            Instrument inst = parser.parse(doc);
            
            if(inst.instrumentHostRef == null) return;
            
            ids.add(inst.id);
            //System.out.println(inst.shortLid + "|" + inst.id + "|" + inst.name + "|" + inst.type + "|" + inst.instrumentHostRef);
        });
        
        
        for(String id: ids)
        {
            System.out.println(id + "->T=2");
        }
    }

    
    public static void extractInstruments() throws Exception
    {
        String dir = "/ws/data/context/pds4/instrument";
        
        // Read file names of latest versions of context products
        Set<String> latestFiles = ExtractContextUtils.readLatestIndex(dir);
    
        // Read host id to mission id map
        Map<String, String> host2mis = MapUtils.loadMap("src/main/data/host2mission.ids");
        
        XmlDomCrawler crawler = new XmlDomCrawler(dir);
        InstrumentParser parser = new InstrumentParser();

        FileWriter writer = new FileWriter("/tmp/solr-docs.xml");
        writer.append("<add>\n");
        
        crawler.crawl((doc, path) -> 
        {
            // Only process the latest versions
            String fileName = path.getFileName().toString();
            if(!latestFiles.contains(fileName)) return;

            Instrument inst = parser.parse(doc);
            
            String hostId = inst.instrumentHostId;
            if(hostId == null || hostId.equals("unk.unk")) 
            {
                //System.out.println("WARNING: No instrument host: " + inst.lid);
                return;
            }
            
            // Housekeeping data
            if("huygens_hk".equals(inst.id)) return;
                                    
            String missionId = host2mis.get(hostId);
            if(missionId == null) 
            {
                System.out.println("WARNING: unknown host id: " + hostId);
                missionId = hostId;
            }

            //debug(inst, hostId, missionId);
            writeSolrDoc(writer, inst, hostId, missionId);
        });
        
        writer.append("</add>\n");
        writer.close();
    }

    
    private static void debug(Instrument inst, String hostId, String missionId) throws Exception
    {
        String name = cleanName(inst.name);
        
        System.out.println(inst.shortLid + "|" + inst.id + "|" + name + "|" + inst.type 
                + "|" + hostId + "|" + missionId);
    }
    
    
    private static void writeSolrDoc(Writer writer, Instrument inst, String hostId, String missionId) throws Exception
    {
        String name = cleanName(inst.name);
        
        writer.append("<doc>\n");

        SolrDocUtils.writeField(writer, "id", inst.shortLid);
        SolrDocUtils.writeField(writer, "lid", inst.lid);
        
        SolrDocUtils.writeField(writer, "instrument_id", inst.id);
        SolrDocUtils.writeField(writer, "instrument_name", name);
        SolrDocUtils.writeField(writer, "instrument_type", inst.type);

        String[] tokens = hostId.split(",");
        for(String id: tokens)
        {
            SolrDocUtils.writeField(writer, "instrument_host_id", id);    
        }
        
        tokens = missionId.split(",");
        for(String id: tokens)
        {
            SolrDocUtils.writeField(writer, "investigation_id", id);
        }

        writer.append("</doc>\n");
    }
    

    private static String cleanName(String str)
    {
        if(str.startsWith("DEEP IMPACT ")) str = str.substring(12);
        else if(str.startsWith("Apollo 12 ") 
                || str.startsWith("Apollo 14 ") || str.startsWith("APOLLO 14 ") 
                || str.startsWith("Apollo 15 ") || str.startsWith("Apollo 16 ")
                || str.startsWith("Apollo 17 ")) str = str.substring(10);
        else if(str.startsWith("MARINER 6 ") || str.startsWith("MARINER 7 ")) str = str.substring(10);
        
        str = WordUtils.capitalizeFully(str);
        return str;
    }
    
    public static void main(String[] args) throws Exception
    {
        extractInstruments();
    }

}
