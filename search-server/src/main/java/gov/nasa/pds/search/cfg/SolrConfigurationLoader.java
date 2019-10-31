package gov.nasa.pds.search.cfg;

import java.io.File;

import javax.xml.xpath.XPathExpression;
import javax.xml.xpath.XPathFactory;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.w3c.dom.Document;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import gov.nasa.pds.search.util.XPathUtils;
import gov.nasa.pds.search.util.XmlDomUtils;

public class SolrConfigurationLoader
{
    private static final Logger LOG = LoggerFactory.getLogger(SolrConfigurationLoader.class);
    
    private SolrConfigurationLoader()
    {        
    }
    
    public static void load(SearchServerConfiguration cfg) throws Exception
    {
        SolrConfiguration solrCfg = cfg.getSolrConfiguration();
        XPathFactory xpf = XPathFactory.newInstance();
        
        File solrCfgFile = new File(cfg.getConfigDirectory(), "solr.xml");
        LOG.info("Reading configuration from " + solrCfgFile.getAbsolutePath());
        if(!solrCfgFile.exists())
        {
            throw new RuntimeException("Configuration file " + solrCfgFile.getAbsolutePath() + " doesn't exist.");
        }
        
        Document doc = XmlDomUtils.readXml(solrCfgFile.getAbsolutePath());
        XPathExpression xpe = XPathUtils.compileXPath(xpf, "/solr/url");
        solrCfg.setUrl(XPathUtils.getStringValue(doc, xpe));
        if(solrCfg.getUrl() == null)
        {
            throw new RuntimeException("Missing Solr URL.");
        }
        
        // Collections
        xpe = XPathUtils.compileXPath(xpf, "/solr/collections/collection");
        NodeList nodes = XPathUtils.getNodeList(doc, xpe);
        if(nodes == null || nodes.getLength() == 0)
        {
            throw new RuntimeException("No collections are configured.");
        }

        for(int i = 0; i < nodes.getLength(); i++)
        {
            Node node = nodes.item(i);
            
            String publicName = node.getAttributes().getNamedItem("publicName").getTextContent();
            if(publicName == null || publicName.isEmpty()) 
            {
                LOG.warn("Missing publicName attribute in /solr/collections/collection.");
                continue;
            }

            SolrCollectionConfiguration cconf = new SolrCollectionConfiguration();
            
            cconf.collectionName = node.getAttributes().getNamedItem("internalName").getTextContent();
            if(cconf.collectionName == null || cconf.collectionName.isEmpty())
            {
                cconf.collectionName = publicName;
            }

            cconf.requestHandler = node.getAttributes().getNamedItem("requestHandler").getTextContent();

            solrCfg.addCollectionConfiguration(publicName, cconf);
        }
    }

}
