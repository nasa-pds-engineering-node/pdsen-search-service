package tt;

import java.io.FileWriter;
import java.io.Writer;
import java.util.Set;
import java.util.TreeSet;

import gov.nasa.pds.data.pds4.model.Instrument;
import gov.nasa.pds.data.pds4.model.ProductCollection;
import gov.nasa.pds.data.pds4.parser.InstrumentParser;
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
            
            if(inst.instrumentHostId == null || inst.instrumentHostId.equals("unk.unk")) 
            {
                //System.out.println("WARNING: No instrument host: " + inst.lid);
                return;
            }
            
            // grail-a & grail-b

            // mer1 & mer2
            
            // vl1 & vl2
            
            // vo1 & vo2
            
            System.out.println(inst.shortLid + "|" + inst.id + "|" + inst.name + "|" + inst.type + "|" + inst.instrumentHostId);
            //writeSolrDoc(writer, inst);
        });
        
        writer.append("</add>\n");
        writer.close();
    }

    
    private static void writeSolrDoc(Writer writer, Instrument inst) throws Exception
    {
        writer.append("<doc>\n");

        SolrDocUtils.writeField(writer, "lid", inst.lid);
        SolrDocUtils.writeField(writer, "vid", inst.vid);
        
        SolrDocUtils.writeField(writer, "id", inst.id);
        SolrDocUtils.writeField(writer, "name", inst.name);
        SolrDocUtils.writeField(writer, "type", inst.type);

        SolrDocUtils.writeField(writer, "instrument_host_id", inst.instrumentHostId);
        SolrDocUtils.writeField(writer, "investigation_id", inst.instrumentHostId);

        writer.append("</doc>\n");
    }
    
    
    
    public static void main(String[] args) throws Exception
    {
        extractInstruments();
    }

}
