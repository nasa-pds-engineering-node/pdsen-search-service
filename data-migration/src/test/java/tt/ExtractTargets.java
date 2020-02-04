package tt;

import java.io.FileWriter;
import java.io.Writer;
import java.util.Set;

import gov.nasa.pds.data.pds4.model.Target;
import gov.nasa.pds.data.pds4.parser.TargetParser;
import gov.nasa.pds.data.util.xml.SolrDocUtils;
import gov.nasa.pds.data.util.xml.XmlDomCrawler;


public class ExtractTargets
{
    public static void extractTargets() throws Exception
    {
        String dir = "/ws/data/context/pds4/target";
        
        // Read file names of latest versions of context products
        Set<String> latestFiles = ExtractContextUtils.readLatestIndex(dir);
        
        XmlDomCrawler crawler = new XmlDomCrawler(dir);
        TargetParser parser = new TargetParser();

        FileWriter writer1 = new FileWriter("/tmp/asteroids.xml");
        FileWriter writer2 = new FileWriter("/tmp/asteroids.dic");
        
        writer1.append("<add>\n");
        
        crawler.crawl((doc, path) -> 
        {
            // Only process the latest versions
            String fileName = path.getFileName().toString();
            if(!latestFiles.contains(fileName)) return;
            
            Target target = parser.parse(doc);
            if("asteroid".equalsIgnoreCase(target.type))
            {
                writeAsteroidSolrDoc(writer1, target);
                writeAsteroidDictionary(writer2, target);
            }
        });
        
        writer1.append("</add>\n");
        writer1.close();
        
        writer2.close();
    }
    
    
    private static void writeAsteroidDictionary(Writer writer, Target tgt) throws Exception
    {
        String id = tgt.shortLid.substring(9);
        
        String tokens[] = id.split("_");
        if(tokens.length == 2)
        {
            writer.write(tokens[1] + "->T=1|I=" + id + "\n");
            writer.write(tokens[0] + "->N=1\n");
            writer.write(tokens[0] + " " + tokens[1] + "->T=1|I=" + id + "\n");
            //writer.write("minor planet " + tokens[0] + "->T=1|I=" + id + "\n");
            writer.write("\n");
        }
    }
    
    
    private static void writeAsteroidSolrDoc(Writer writer, Target tgt) throws Exception
    {
        writer.append("<doc>\n");

        SolrDocUtils.writeField(writer, "id", tgt.shortLid);
        SolrDocUtils.writeField(writer, "lid", tgt.lid);

        // Target IDs
        String id = tgt.shortLid.substring(9);
        SolrDocUtils.writeField(writer, "target_id", id);
        
        String tokens[] = id.split("_");
        if(tokens.length == 2)
        {
            SolrDocUtils.writeField(writer, "target_id", tokens[1]);
        }
        
        SolrDocUtils.writeField(writer, "target_type", "asteroid");
        SolrDocUtils.writeField(writer, "target_type", "minor_planet");
        
        SolrDocUtils.writeField(writer, "title", tgt.title);
        
        writer.append("</doc>\n");
    }
    
    
    public static void main(String[] args) throws Exception
    {
        extractTargets();
    }

}
