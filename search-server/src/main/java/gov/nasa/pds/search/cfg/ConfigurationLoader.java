package gov.nasa.pds.search.cfg;

import java.io.File;
import java.util.ArrayList;

import javax.xml.xpath.XPathExpression;
import javax.xml.xpath.XPathFactory;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.w3c.dom.Document;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import gov.nasa.pds.search.util.NameMapper;
import gov.nasa.pds.search.util.XPathUtils;
import gov.nasa.pds.search.util.XmlDomUtils;


/**
 * Loads PDS search server configuration from a location provided in either 
 * JVM '-D' argument or environment variable.
 * @author karpenko
 */
public class ConfigurationLoader
{
    private static final String D_CONF = "pds.search.server.conf";
    private static final String ENV_CONF = "PDS_SEARCH_SERVER_CONF";
        
    private static final Logger LOG = LoggerFactory.getLogger(ConfigurationLoader.class);
    
    /**
     * Private constructor.
     */
    private ConfigurationLoader()
    {
    }
    
    /**
     * Loads PDS search server configuration
     * @return
     */
    public static SearchServerConfiguration load() throws Exception
    {
        String path = getConfigPath();
        
        File configDir = new File(path);
        if(!configDir.isDirectory())
        {
            throw new RuntimeException("Configuration path must be a directory: " + path);
        }
        
        SearchServerConfiguration cfg = new SearchServerConfiguration(configDir);
        loadSolrConfiguration(cfg);
        loadFieldConfiguration(cfg);
        loadGeoConfiguration(cfg);
        
        return cfg;
    }
    
    
    private static String getConfigPath()
    {
        String path = System.getProperty(D_CONF);
        if(path == null)
        {
            path = System.getenv(ENV_CONF);
        }
        
        if(path == null)
        {
            throw new RuntimeException("Missing JVM argument -D" + D_CONF + " or environment variable " + ENV_CONF);
        }

        return path;
    }
        
    
    private static void loadSolrConfiguration(SearchServerConfiguration cfg) throws Exception
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
    
    
    private static void loadFieldConfiguration(SearchServerConfiguration cfg) throws Exception
    {
        FieldConfiguration fieldCfg = cfg.getFieldConfiguration();
        XPathFactory xpf = XPathFactory.newInstance();

        File fieldMapFile = new File(cfg.getConfigDirectory(), "fields.xml");
        LOG.info("Reading configuration from " + fieldMapFile.getAbsolutePath());
        if(!fieldMapFile.exists())
        {
            LOG.info("File doesn't exist.");
            return;
        }        

        Document doc = XmlDomUtils.readXml(fieldMapFile.getAbsolutePath());

        // Field Map
        XPathExpression xpe = XPathUtils.compileXPath(xpf, "/fields/fieldMap/field");
        NodeList nodes = XPathUtils.getNodeList(doc, xpe);
        if(nodes != null && nodes.getLength() > 0)
        {
            fieldCfg.nameMapper = new NameMapper();
            
            for(int i = 0; i < nodes.getLength(); i++)
            {
                Node node = nodes.item(i);
                
                String publicName = node.getAttributes().getNamedItem("publicName").getTextContent();
                if(publicName == null || publicName.isEmpty()) 
                {
                    LOG.warn("Missing publicName attribute in /fields/fieldMap/field.");
                    continue;
                }
                
                String internalName = node.getAttributes().getNamedItem("internalName").getTextContent();
                if(internalName == null || internalName.isEmpty())
                {
                    LOG.warn("Missing internalName attribute in /fields/fieldMap/field.");
                    continue;
                }
                
                fieldCfg.nameMapper.addPublicAndInternal(publicName, internalName);
            }
        }
        
        // Default fields
        xpe = XPathUtils.compileXPath(xpf, "/fields/defaultFields/field");
        nodes = XPathUtils.getNodeList(doc, xpe);
        if(nodes != null && nodes.getLength() > 0)
        {
            fieldCfg.defaultFields = new ArrayList<>();
            
            for(int i = 0; i < nodes.getLength(); i++)
            {
                Node node = nodes.item(i);
                
                String internalName = node.getAttributes().getNamedItem("internalName").getTextContent();
                if(internalName == null || internalName.isEmpty()) 
                {
                    LOG.warn("Missing internalName attribute in /fields/defaultFields/field.");
                    continue;
                }
                
                fieldCfg.defaultFields.add(internalName);
            }
        }
    }
    
    
    private static void loadGeoConfiguration(SearchServerConfiguration cfg) throws Exception
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
