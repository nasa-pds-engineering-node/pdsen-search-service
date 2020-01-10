package gov.nasa.pds.data.pds4.parser;

import javax.xml.xpath.XPathExpression;
import javax.xml.xpath.XPathFactory;

import org.w3c.dom.Document;

import gov.nasa.pds.data.pds4.model.ProductCollection;
import gov.nasa.pds.data.util.xml.XPathUtils;


public class ProductCollectionParser
{
    private XPathExpression xLid;
    private XPathExpression xVid;
    private XPathExpression xTitle;
    
    private XPathExpression xType;
    private XPathExpression xDescr;

    private XPathExpression xScienceFacets;
    private XPathExpression xProcessingLevel;
    
    private XPathExpression xInvestigationRef;
    private XPathExpression xInstrumentHostRef;
    private XPathExpression xInstrumentRef;
    private XPathExpression xTargetRef;

    
    public ProductCollectionParser() throws Exception
    {
        XPathFactory xpf = XPathFactory.newInstance();
    
        xLid = XPathUtils.compileXPath(xpf, "/Product_Collection/Identification_Area/logical_identifier");
        xVid = XPathUtils.compileXPath(xpf, "/Product_Collection/Identification_Area/version_id");

        xTitle = XPathUtils.compileXPath(xpf, "/Product_Collection/Identification_Area/title");
        
        xType = XPathUtils.compileXPath(xpf, "/Product_Collection/Collection/collection_type");
        xDescr = XPathUtils.compileXPath(xpf, "/Product_Collection/Collection/description");

        // Results
        xProcessingLevel = XPathUtils.compileXPath(xpf, "/Product_Collection/Context_Area/Primary_Result_Summary/processing_level");
        xScienceFacets = XPathUtils.compileXPath(xpf, "/Product_Collection/Context_Area/Primary_Result_Summary/Science_Facets");
        
        // References
        xInvestigationRef = XPathUtils.compileXPath(xpf, "//Internal_Reference[reference_type='collection_to_investigation']/lid_reference");
        xInstrumentHostRef = XPathUtils.compileXPath(xpf, "//Internal_Reference[reference_type='is_instrument_host']/lid_reference");
        xInstrumentRef = XPathUtils.compileXPath(xpf, "//Internal_Reference[reference_type='is_instrument']/lid_reference");
        xTargetRef = XPathUtils.compileXPath(xpf, "//Internal_Reference[reference_type='collection_to_target']/lid_reference");
    }

    
    public ProductCollection parse(Document doc) throws Exception
    {
        ProductCollection pc = new ProductCollection();
        
        pc.lid = XPathUtils.getStringValue(doc, xLid);
        String strVid = XPathUtils.getStringValue(doc, xVid); 
        pc.vid = Float.parseFloat(strVid);

        pc.title = XPathUtils.getStringValue(doc, xTitle);
        pc.type = XPathUtils.getStringValue(doc, xType);
        pc.description = XPathUtils.getStringValue(doc, xDescr);

        pc.processingLevel = XPathUtils.getStringValue(doc, xProcessingLevel);
        pc.scienceFacets = XPathUtils.getChildValues(doc, xScienceFacets);
        
        // References
        pc.investigationRef = XPathUtils.getStringArray(doc, xInvestigationRef);
        pc.instrumentHostRef = XPathUtils.getStringArray(doc, xInstrumentHostRef);
        pc.instrumentRef = XPathUtils.getStringArray(doc, xInstrumentRef);
        pc.targetRef = XPathUtils.getStringArray(doc, xTargetRef);
        
        return pc;
    }
}
