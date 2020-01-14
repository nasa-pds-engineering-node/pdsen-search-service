package tt;

import gov.nasa.pds.data.pds4.model.ProductCollection;
import gov.nasa.pds.data.pds4.parser.ProductCollectionParser;
import gov.nasa.pds.data.pds4.solr.ProductCollectionWriter;
import gov.nasa.pds.data.util.xml.XmlDomCrawler;


public class ExtractCollections
{
    private static String[] defInvestigationRef = { "urn:nasa:pds:context:investigation:mission.maven" };
    private static String[] defInstrumentHostRef = { "urn:nasa:pds:context:instrument_host:spacecraft.maven" };
    
    
    public static void main(String[] args) throws Exception
    {
        //String dir = "/ws3/MAVEN/mag";
        String dir = "/ws3/OREX/";
        String outFile = "/tmp/orex.xml";
        
        XmlDomCrawler crawler = new XmlDomCrawler(dir);
        ProductCollectionWriter writer = new ProductCollectionWriter(outFile);
        ProductCollectionParser parser = new ProductCollectionParser();
        
        crawler.crawl((doc, path) -> 
        {
            String rootElementName = doc.getDocumentElement().getNodeName();
            if(!"Product_Collection".equals(rootElementName)) return;
            
            ProductCollection pc = parser.parse(doc);
            
            // Fix document collections
            if(pc.type.equals("Document")) 
            {
                //validateAndFixDocumentCollection(pc);
                return;
            }
            
            writer.write(pc);
        });
        
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
    
}

