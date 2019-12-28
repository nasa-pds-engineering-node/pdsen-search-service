package gov.nasa.pds.data.pds4.parser;

import javax.xml.xpath.XPathExpression;
import javax.xml.xpath.XPathFactory;

import org.w3c.dom.Document;

import gov.nasa.pds.data.pds4.model.Target;
import gov.nasa.pds.data.util.xml.XPathUtils;


public class TargetParser
{
    private XPathExpression xLid;
    private XPathExpression xVid;
    
    private XPathExpression xTitle;
    private XPathExpression xAltTitle;

    private XPathExpression xName;
    private XPathExpression xType;
    private XPathExpression xDescr;

    
    public TargetParser() throws Exception
    {
        XPathFactory xpf = XPathFactory.newInstance();

        xLid = XPathUtils.compileXPath(xpf, "//Identification_Area/logical_identifier");
        xVid = XPathUtils.compileXPath(xpf, "//Identification_Area/version_id");
        
        xTitle = XPathUtils.compileXPath(xpf, "//Identification_Area/title");
        xAltTitle = XPathUtils.compileXPath(xpf, "//Identification_Area/Alias_List/Alias/alternate_title");
        
        xName = XPathUtils.compileXPath(xpf, "//Instrument_Host/name");
        xType = XPathUtils.compileXPath(xpf, "//Instrument_Host/type");
        xDescr = XPathUtils.compileXPath(xpf, "//Instrument_Host/description");
    }
    
    
    public Target parse(Document doc) throws Exception
    {
        Target obj = new Target();

        // Lid
        obj.lid = XPathUtils.getStringValue(doc, xLid);
        obj.shortLid = getShortLid(obj.lid);
        
        // Vid
        String strVid = XPathUtils.getStringValue(doc, xVid); 
        obj.vid = Float.parseFloat(strVid);

        // Name
        addName(obj, XPathUtils.getStringValue(doc, xName));
        addName(obj, XPathUtils.getStringValue(doc, xTitle));
        addName(obj, XPathUtils.getStringArray(doc, xAltTitle));
        
        // Other
        obj.type = XPathUtils.getStringValue(doc, xType);
        obj.description = XPathUtils.getStringValue(doc, xDescr);

        return obj;
    }
    
    
    private static String getShortLid(String lid)
    {
        int idx = lid.indexOf(":target:");
        if(idx > 0) return lid.substring(idx + 8);

        return null;
    }
    
    
    private static void addName(Target tgt, String... name)
    {
        
    }
}
