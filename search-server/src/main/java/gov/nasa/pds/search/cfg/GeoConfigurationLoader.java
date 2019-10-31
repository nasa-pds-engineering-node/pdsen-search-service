package gov.nasa.pds.search.cfg;

import java.io.File;

import javax.xml.xpath.XPathExpression;
import javax.xml.xpath.XPathFactory;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.w3c.dom.Document;

import gov.nasa.pds.search.util.XPathUtils;
import gov.nasa.pds.search.util.XmlDomUtils;


public class GeoConfigurationLoader
{
    private static final Logger LOG = LoggerFactory.getLogger(GeoConfigurationLoader.class);
    
    
    private GeoConfigurationLoader()
    {        
    }
    

    public static void load(SearchServerConfiguration cfg) throws Exception
    {
        File geoCfgFile = new File(cfg.getConfigDirectory(), "geo.xml");
        if(!geoCfgFile.exists())
        {
            // Geo configuration is optional
            return;
        }

        LOG.info("Reading configuration from " + geoCfgFile.getAbsolutePath());
        Document doc = XmlDomUtils.readXml(geoCfgFile.getAbsolutePath());

        GeoConfiguration geoCfg = cfg.getGeoConfiguration();

        XPathFactory xpf = XPathFactory.newInstance();
        
        // URL
        XPathExpression xpe = XPathUtils.compileXPath(xpf, "/geo/url");
        geoCfg.url = XPathUtils.getStringValue(doc, xpe);
        if(geoCfg.url == null)
        {
            throw new RuntimeException("Missing Geo URL.");
        }
        
        // Timeout
        xpe = XPathUtils.compileXPath(xpf, "/geo/timeoutSec");
        String strVal = XPathUtils.getStringValue(doc, xpe);
        if(strVal == null)
        {
            // Use default
            geoCfg.timeoutSec = 5;
        }
        else
        {
            try
            {
                geoCfg.timeoutSec = Integer.parseInt(strVal);
            }
            catch(Exception ex)
            {
                throw new RuntimeException("Invalid '/geo/timeoutSec' value " + strVal);
            }
        
            if(geoCfg.timeoutSec < 1)
            {
                throw new RuntimeException("Invalid '/geo/timeoutSec' value " + strVal);
            }
        }
    }

}
