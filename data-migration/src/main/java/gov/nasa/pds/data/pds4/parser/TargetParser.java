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

    private XPathExpression xType;

    
    public TargetParser() throws Exception
    {
        XPathFactory xpf = XPathFactory.newInstance();

        xLid = XPathUtils.compileXPath(xpf, "//Identification_Area/logical_identifier");
        xVid = XPathUtils.compileXPath(xpf, "//Identification_Area/version_id");
        xTitle = XPathUtils.compileXPath(xpf, "//Identification_Area/title");
        
        xType = XPathUtils.compileXPath(xpf, "/Product_Context/Target/type");
    }
    
    
    public Target parse(Document doc) throws Exception
    {
        Target obj = new Target();

        // Lid
        obj.lid = XPathUtils.getStringValue(doc, xLid);
        obj.shortLid = ParserUtils.getShortLid(obj.lid);

        // Vid
        String strVid = XPathUtils.getStringValue(doc, xVid); 
        obj.vid = Float.parseFloat(strVid);

        obj.title = XPathUtils.getStringValue(doc, xTitle);
        obj.type = XPathUtils.getStringValue(doc, xType);

        return obj;
    }

}
