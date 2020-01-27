package gov.nasa.pds.data.pds4.parser;

import javax.xml.xpath.XPathExpression;
import javax.xml.xpath.XPathFactory;

import org.w3c.dom.Document;

import gov.nasa.pds.data.pds3.parser.Pds3Utils;
import gov.nasa.pds.data.pds4.model.Investigation;
import gov.nasa.pds.data.util.xml.XPathUtils;


public class InvestigationParser
{
    private XPathExpression xLid;
    private XPathExpression xVid;

    private XPathExpression xName;
    private XPathExpression xType;
    private XPathExpression xDescr;

    
    public InvestigationParser() throws Exception
    {
        XPathFactory xpf = XPathFactory.newInstance();

        xLid = XPathUtils.compileXPath(xpf, "//Identification_Area/logical_identifier");
        xVid = XPathUtils.compileXPath(xpf, "//Identification_Area/version_id");
        
        xName = XPathUtils.compileXPath(xpf, "//Investigation/name");
        xType = XPathUtils.compileXPath(xpf, "//Investigation/type");
        xDescr = XPathUtils.compileXPath(xpf, "//Investigation/description");
    }
    
    
    public Investigation parse(Document doc) throws Exception
    {
        Investigation obj = new Investigation();

        obj.lid = XPathUtils.getStringValue(doc, xLid);
        obj.shortLid = Pds3Utils.getShortLid(obj.lid);        
        obj.vid = XPathUtils.getStringValue(doc, xVid); 

        obj.name = XPathUtils.getStringValue(doc, xName);
        obj.type = XPathUtils.getStringValue(doc, xType);
        obj.description = XPathUtils.getStringValue(doc, xDescr);
        
        return obj;
    }
    
}
