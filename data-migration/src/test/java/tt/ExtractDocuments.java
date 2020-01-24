package tt;

import gov.nasa.pds.data.pds4.model.ProductCollection;
import gov.nasa.pds.data.pds4.parser.ProductCollectionParser;
import gov.nasa.pds.data.pds4.solr.ProductCollectionWriter;
import gov.nasa.pds.data.util.xml.XmlDomCrawler;


public class ExtractDocuments
{
    private static String[] MAVEN_INVEST = { "urn:nasa:pds:context:investigation:mission.maven" };
    private static String[] MAVEN_INST_HOST = { "urn:nasa:pds:context:instrument_host:spacecraft.maven" };
    
    
    public static void main(String[] args) throws Exception
    {
        //String dir = "/ws3/MAVEN/mag";
        String dir = "/ws3/OREX/";
        String outFile = "/tmp/orex_doc.xml";
        
        XmlDomCrawler crawler = new XmlDomCrawler(dir);
        ProductCollectionWriter writer = new ProductCollectionWriter(outFile);
        ProductCollectionParser parser = new ProductCollectionParser();
        
        crawler.crawl((doc, path) -> 
        {
            String rootElementName = doc.getDocumentElement().getNodeName();
            if("Product_Collection".equals(rootElementName))
            {
                ProductCollection pc = parser.parse(doc);
                if("document".equalsIgnoreCase(pc.type))
                {
                    validateAndFixDocumentCollection(pc);
                    writer.write(pc);        
                }                
            }
            else if("Product_Document".equals(rootElementName))
            {
                //TODO: Implement
            }
        });
        
        writer.close();
    }
    
    
    private static void validateAndFixDocumentCollection(ProductCollection pc)
    {
        if(pc.investigationRef == null) 
        {
            System.out.println("WARNING: Missing investigation_id: LID = " + pc.lid);
            pc.investigationRef = getInvestigationIdByLid(pc.lid);
        }
        
        if(pc.instrumentRef != null && pc.instrumentHostRef == null)
        {
            System.out.println("WARNING: Missing instrument_host_id: LID = " + pc.lid);
            pc.instrumentHostRef = getInstHostIdByLid(pc.lid);
        }
    }
    
    
    private static String[] getInvestigationIdByLid(String lid)
    {
        if(lid.startsWith("urn:nasa:pds:maven")) return MAVEN_INVEST;
        
        return null;
    }


    private static String[] getInstHostIdByLid(String lid)
    {
        if(lid.startsWith("urn:nasa:pds:maven")) return MAVEN_INST_HOST;
        
        return null;
    }

}

