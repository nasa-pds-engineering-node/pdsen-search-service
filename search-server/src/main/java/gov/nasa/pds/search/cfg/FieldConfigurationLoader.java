package gov.nasa.pds.search.cfg;

import java.io.File;
import java.util.ArrayList;
import java.util.HashSet;

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


public class FieldConfigurationLoader
{
    private static final Logger LOG = LoggerFactory.getLogger(FieldConfigurationLoader.class);
    
    
    private FieldConfigurationLoader()
    {        
    }
    
    
    public static void load(SearchServerConfiguration cfg) throws Exception
    {
        File fieldMapFile = new File(cfg.getConfigDirectory(), "fields.xml");
        LOG.info("Reading configuration from " + fieldMapFile.getAbsolutePath());
        if(!fieldMapFile.exists())
        {
            LOG.info("File doesn't exist.");
            return;
        }        

        Document doc = XmlDomUtils.readXml(fieldMapFile.getAbsolutePath());
        FieldConfiguration fieldCfg = cfg.getFieldConfiguration();

        loadFieldMap(fieldCfg, doc);
        loadDefaultFields(fieldCfg, doc);
        loadSearchFields(fieldCfg, doc);
    }

    
    private static void loadSearchFields(FieldConfiguration fieldCfg, Document doc) throws Exception
    {
        XPathFactory xpf = XPathFactory.newInstance();

        XPathExpression xpe = XPathUtils.compileXPath(xpf, "/fields/searchFields/field");
        NodeList nodes = XPathUtils.getNodeList(doc, xpe);
        
        if(nodes != null && nodes.getLength() > 0)
        {
            fieldCfg.searchFields = new HashSet<>();
            
            for(int i = 0; i < nodes.getLength(); i++)
            {
                Node node = nodes.item(i);
                
                String internalName = node.getAttributes().getNamedItem("internalName").getTextContent();
                if(internalName == null || internalName.isEmpty()) 
                {
                    LOG.warn("Missing internalName attribute in /fields/searchFields/field.");
                    continue;
                }
                
                fieldCfg.searchFields.add(internalName);
            }
        }
        else
        {
            throw new RuntimeException("Missing search fields.");
        }
    }
    
    
    private static void loadDefaultFields(FieldConfiguration fieldCfg, Document doc) throws Exception
    {
        XPathFactory xpf = XPathFactory.newInstance();

        XPathExpression xpe = XPathUtils.compileXPath(xpf, "/fields/defaultFields/field");
        NodeList nodes = XPathUtils.getNodeList(doc, xpe);
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
    
    
    private static void loadFieldMap(FieldConfiguration fieldCfg, Document doc) throws Exception
    {
        XPathFactory xpf = XPathFactory.newInstance();

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
    }
}
