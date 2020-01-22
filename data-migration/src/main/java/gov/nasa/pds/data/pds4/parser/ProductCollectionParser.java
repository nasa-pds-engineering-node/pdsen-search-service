package gov.nasa.pds.data.pds4.parser;

import javax.xml.xpath.XPathExpression;
import javax.xml.xpath.XPathFactory;

import org.apache.commons.lang.StringUtils;
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
    private XPathExpression xCitationDescr;
    
    private XPathExpression xKeyword;
    private XPathExpression xPurpose;
    private XPathExpression xScienceFacets;
    private XPathExpression xProcessingLevel;

    // References
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
        xCitationDescr = XPathUtils.compileXPath(xpf, "/Product_Collection/Identification_Area/Citation_Information/description");     
        
        // Results
        xKeyword = XPathUtils.compileXPath(xpf, "/Product_Collection/Identification_Area/Citation_Information/keyword");
        xPurpose = XPathUtils.compileXPath(xpf, "/Product_Collection/Context_Area/Primary_Result_Summary/purpose");
        xProcessingLevel = XPathUtils.compileXPath(xpf, "/Product_Collection/Context_Area/Primary_Result_Summary/processing_level");
        xScienceFacets = XPathUtils.compileXPath(xpf, "/Product_Collection/Context_Area/Primary_Result_Summary/Science_Facets");
        
        // References
        xInvestigationRef = XPathUtils.compileXPath(xpf, "//Internal_Reference[reference_type='collection_to_investigation']/lid_reference");
        xInstrumentHostRef = XPathUtils.compileXPath(xpf, "//Internal_Reference[reference_type='is_instrument_host']/lid_reference");
        xInstrumentRef = XPathUtils.compileXPath(xpf, "//Internal_Reference[reference_type='is_instrument']/lid_reference");
        xTargetRef = XPathUtils.compileXPath(xpf, "//Internal_Reference[reference_type='collection_to_target']/lid_reference");
    }

    
    private String normalizeSpaces(String str)
    {
        if(str == null) return null;

        str = StringUtils.normalizeSpace(str);
        if(str.isEmpty()) return null;
        
        return str;
    }
    
    
    /**
     * This function keeps the order of strings if both str1 and str2 present.
     * I could have used a Set and a List instead, but few if statements are simpler.
     */
    private String[] createUniqueArray(String str1, String str2)
    {
        // Both strings are null
        if(str1 == null && str2 == null) return null;
        
        // Only one is null
        if(str1 == null) return new String[] {str2};
        if(str2 == null) return new String[] {str1};
        
        // Both are not null
        if(str1.equals(str2)) return new String[] {str1};
        return new String[] {str1, str2};
    }

    
    private String normalizeCollectionType(String str)
    {
        if(str == null) return "data";
        
        str = str.toLowerCase();
        str = str.replace(' ', '_');
        
        return str;
    }
    
    
    public ProductCollection parse(Document doc) throws Exception
    {
        ProductCollection pc = new ProductCollection();
        
        pc.lid = XPathUtils.getStringValue(doc, xLid);
        String strVid = XPathUtils.getStringValue(doc, xVid); 
        pc.vid = Float.parseFloat(strVid);

        pc.title = normalizeSpaces(XPathUtils.getStringValue(doc, xTitle));
        pc.type = normalizeCollectionType(XPathUtils.getStringValue(doc, xType));
        
        // Description
        String descr1 = normalizeSpaces(XPathUtils.getStringValue(doc, xCitationDescr));
        String descr2 = normalizeSpaces(XPathUtils.getStringValue(doc, xDescr));
        pc.description = createUniqueArray(descr1, descr2);

        // Results
        pc.keywords = XPathUtils.getStringArray(doc, xKeyword);
        pc.purpose = XPathUtils.getStringValue(doc, xPurpose);
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
