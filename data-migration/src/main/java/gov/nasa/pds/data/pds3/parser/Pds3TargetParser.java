package gov.nasa.pds.data.pds3.parser;

import javax.xml.xpath.XPathExpression;
import javax.xml.xpath.XPathFactory;
import org.w3c.dom.Document;

import gov.nasa.pds.data.pds3.model.Pds3Instrument;
import gov.nasa.pds.data.pds3.model.Pds3Target;
import gov.nasa.pds.data.util.xml.XPathUtils;


public class Pds3TargetParser
{
    private XPathExpression xLid;
    private XPathExpression xTitle;

    private XPathExpression xName;
    private XPathExpression xType;
    
    private XPathExpression xPrimBodyName;

    
    public Pds3TargetParser() throws Exception
    {
        XPathFactory xpf = XPathFactory.newInstance();

        xLid = XPathUtils.compileXPath(xpf, "//Identification_Area/logical_identifier");
        xTitle = XPathUtils.compileXPath(xpf, "//Identification_Area/title");

        xName = XPathUtils.compileXPath(xpf, "//Target_PDS3/target_name");
        xType = XPathUtils.compileXPath(xpf, "//Target_PDS3/target_type");
        xPrimBodyName = XPathUtils.compileXPath(xpf, "//Target_PDS3/primary_body_name");
    }

    
    public Pds3Target parse(Document doc) throws Exception
    {
        Pds3Target obj = new Pds3Target();

        obj.lid = XPathUtils.getStringValue(doc, xLid);
        obj.shortLid = Pds3Utils.getShortLid(obj.lid);
        obj.title = XPathUtils.getStringValue(doc, xTitle);

        obj.name = XPathUtils.getStringValue(doc, xName);
        obj.type = XPathUtils.getStringValue(doc, xType);
        obj.primaryBodyName = XPathUtils.getStringValue(doc, xPrimBodyName);

        return obj;
    }
}
