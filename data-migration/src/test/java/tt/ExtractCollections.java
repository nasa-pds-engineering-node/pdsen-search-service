package tt;

import java.io.FileWriter;
import java.io.Writer;

import gov.nasa.pds.data.pds4.model.ProductCollection;
import gov.nasa.pds.data.pds4.parser.ParserUtils;
import gov.nasa.pds.data.pds4.parser.ProductCollectionParser;
import gov.nasa.pds.data.util.xml.SolrDocUtils;
import gov.nasa.pds.data.util.xml.XmlDomCrawler;

public class ExtractCollections
{
    private static String[] defInvestigationRef = { "urn:nasa:pds:context:investigation:mission.maven" };
    private static String[] defInstrumentHostRef = { "urn:nasa:pds:context:instrument_host:spacecraft.maven" };
    
    
    public static void main(String[] args) throws Exception
    {
        //String dir = "/ws3/MAVEN/rose";
        String dir = "/ws3/MAVEN/";
        XmlDomCrawler crawler = new XmlDomCrawler(dir);
        
        ProductCollectionParser parser = new ProductCollectionParser();

        FileWriter writer = new FileWriter("/tmp/maven.xml");
        writer.append("<add>\n");
        
        crawler.crawl((doc, path) -> 
        {
            String rootElementName = doc.getDocumentElement().getNodeName();
            if(!"Product_Collection".equals(rootElementName)) return;
            
            ProductCollection pc = parser.parse(doc);
            
            // Fix document collections
            if(pc.type.equals("Document")) 
            {
                validateAndFixDocumentCollection(pc);
            }
            
            writeSolrDoc(writer, pc);
        });
        
        writer.append("</add>\n");
        writer.close();
    }
    
    
    private static void validateAndFixDocumentCollection(ProductCollection pc)
    {
        if(pc.investigationRef == null) 
        {
            System.out.println("WARNING: Missing investigation_id: LID = " + pc.lid);
            // TODO: Fix
            pc.investigationRef = defInvestigationRef;
        }
        
        if(pc.instrumentRef != null && pc.instrumentHostRef == null)
        {
            System.out.println("WARNING: Missing instrument_host_id: LID = " + pc.lid);
            // TODO: Fix
            pc.instrumentHostRef = defInstrumentHostRef;
        }
    }
    
    
    private static void writeSolrDoc(Writer writer, ProductCollection pc) throws Exception
    {
        writer.append("<doc>\n");

        SolrDocUtils.writeField(writer, "lid", pc.lid);
        SolrDocUtils.writeField(writer, "vid", pc.vid);
        SolrDocUtils.writeField(writer, "product_class", "Product_Collection");
        SolrDocUtils.writeField(writer, "title", pc.title);
        SolrDocUtils.writeField(writer, "collection_type", pc.type);
        
        writeInvestigation(writer, pc);
        writeInstrumentHost(writer, pc);
        writeInstruments(writer, pc);
        writeTargets(writer, pc);

        writer.append("</doc>\n");
    }

    
    private static void writeInvestigation(Writer writer, ProductCollection pc) throws Exception
    {
        if(pc.investigationRef == null) return;

        for(String ref: pc.investigationRef)
        {
            String shortLid = ParserUtils.getShortLid(ref);
            
            String id = ParserUtils.getInvestigationId(shortLid);
            if(id != null)
            {
                SolrDocUtils.writeField(writer, "investigation_id", id);
            }
        }
    }

    
    private static void writeInstrumentHost(Writer writer, ProductCollection pc) throws Exception
    {
        if(pc.instrumentHostRef == null) return;

        for(String ref: pc.instrumentHostRef)
        {
            String shortLid = ParserUtils.getShortLid(ref);
            
            String id = ParserUtils.getInstrumentHostId(shortLid);
            if(id != null)
            {
                SolrDocUtils.writeField(writer, "instrument_host_id", id);
            }
        }
    }

    
    private static void writeInstruments(Writer writer, ProductCollection pc) throws Exception
    {
        if(pc.instrumentRef == null) return;

        for(String ref: pc.instrumentRef)
        {
            String shortLid = ParserUtils.getShortLid(ref);

            String id = ParserUtils.getInstrumentId(shortLid);
            if(id != null)
            {
                SolrDocUtils.writeField(writer, "instrument_id", id);
            }
        }
    }
    
    
    private static void writeTargets(Writer writer, ProductCollection pc) throws Exception
    {
        if(pc.targetRef == null) return;

        for(String ref: pc.targetRef)
        {
            String shortLid = ParserUtils.getShortLid(ref);

            String id = ParserUtils.getTargetId(shortLid);
            if(id != null)
            {
                SolrDocUtils.writeField(writer, "target", id);
            }
        }
    }

}

