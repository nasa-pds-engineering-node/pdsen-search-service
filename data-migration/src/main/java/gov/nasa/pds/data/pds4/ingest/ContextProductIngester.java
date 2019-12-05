package gov.nasa.pds.data.pds4.ingest;

import java.nio.file.Path;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.w3c.dom.Document;

import gov.nasa.pds.data.pds4.dao.InstrumentHostDAO;
import gov.nasa.pds.data.pds4.dao.InstrumentHostDAO_Solr;
import gov.nasa.pds.data.pds4.model.InstrumentHost;
import gov.nasa.pds.data.pds4.parser.InstrumentHostParser;
import gov.nasa.pds.data.pds4.parser.ProductClassParser;
import gov.nasa.pds.data.util.xml.XmlDomCrawler;


public class ContextProductIngester
{
    private static final Logger LOG = Logger.getLogger(ContextProductIngester.class.getName());
    
    private ProductClassParser pcParser;
    private InstrumentHostParser ihParser;

    private InstrumentHostDAO ihDao;
    
    public ContextProductIngester() throws Exception
    {
        pcParser = new ProductClassParser();
        ihParser = new InstrumentHostParser();
        
        ihDao = new InstrumentHostDAO_Solr();
    }
    
    
    public void crawl(String directory) throws Exception
    {
        XmlDomCrawler crawler = new XmlDomCrawler(directory);
        crawler.crawl((doc, path) -> 
        {
            String rootTag = doc.getDocumentElement().getTagName();
            if(!rootTag.equals("Product_Context"))
            {
                LOG.info("Skipping " + path);
            }
            else
            {
                try
                {
                    onDocument(doc, path);
                }
                catch(Exception ex)
                {
                    LOG.log(Level.SEVERE, "Could not load " + path, ex);
                }
            }
        });
    }
    
    
    private void onDocument(Document doc, Path path) throws Exception
    {
        // Get product class
        String productClass = pcParser.getProductClass(doc);

        if(productClass == null)
        {
            LOG.warning("Unsupported product class. Skipping " + path);
            return;
        }
        
        switch(productClass)
        {
        case "Investigation":
            break;
        case "Instrument":
            break;
        case "Instrument_Host":
            InstrumentHost ih = ihParser.parse(doc);
            ihDao.update(ih);
            break;
        case "Target":
            break;
        }
    }
}
