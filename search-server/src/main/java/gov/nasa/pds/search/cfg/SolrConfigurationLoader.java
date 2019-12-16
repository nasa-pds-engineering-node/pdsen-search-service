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
            
            Node tmpNode = node.getAttributes().getNamedItem("id");
            String id = (tmpNode == null) ? null : tmpNode.getTextContent();
            if(id == null || id.isEmpty()) 
            {
                throw new RuntimeException("Missing 'id' attribute in /solr/collections/collection tag.");
            }

            SolrCollectionConfiguration cconf = new SolrCollectionConfiguration();
            
            tmpNode = node.getAttributes().getNamedItem("name");
            cconf.collectionName = (tmpNode == null) ? null : tmpNode.getTextContent();
            if(cconf.collectionName == null || cconf.collectionName.isEmpty())
            {
                throw new RuntimeException("Missing 'name' attribute in /solr/collections/collection tag.");
            }
            
            tmpNode = node.getAttributes().getNamedItem("requestHandler");
            cconf.requestHandler = (tmpNode == null) ? null : tmpNode.getTextContent();

            solrCfg.addCollectionConfiguration(id, cconf);
        }
    }

}
