package tt;

import gov.nasa.pds.data.pds4.model.ProductCollection;
import gov.nasa.pds.data.pds4.parser.ProductCollectionParser;
import gov.nasa.pds.data.pds4.solr.ProductCollectionWriter;
import gov.nasa.pds.data.util.xml.XmlDomCrawler;


public class ExtractDataCollections
{
    
    public static void main(String[] args) throws Exception
    {
        //String dir = "/ws3/MAVEN/mag";
        String dir = "/ws3/MAVEN/";
        String outFile = "/tmp/maven.xml";
        
        XmlDomCrawler crawler = new XmlDomCrawler(dir);
        ProductCollectionWriter writer = new ProductCollectionWriter(outFile);
        ProductCollectionParser parser = new ProductCollectionParser();
        
        crawler.crawl((doc, path) -> 
        {
            String rootElementName = doc.getDocumentElement().getNodeName();
            if(!"Product_Collection".equals(rootElementName)) return;
            
            ProductCollection pc = parser.parse(doc);
            
            // Skip document collections
            if(pc.type.equalsIgnoreCase("document")) 
            {
                return;
            }
            
            validateAndFixCollection(pc);
            writer.write(pc);
        });
        
        writer.close();
    }
    
    
    private static void validateAndFixCollection(ProductCollection pc)
    {
        if(pc.purpose == null)
        {
            pc.purpose = "Science";
            System.out.println("Primary_Result_Summary/purpose is missing for " + pc.lid);
        }
    }
    

}

