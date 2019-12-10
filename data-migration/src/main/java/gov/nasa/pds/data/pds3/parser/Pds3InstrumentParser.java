package gov.nasa.pds.data.pds3.parser;

import javax.xml.xpath.XPathExpression;
import javax.xml.xpath.XPathFactory;
import org.w3c.dom.Document;

import gov.nasa.pds.data.pds3.model.Pds3Instrument;
import gov.nasa.pds.data.util.xml.XPathUtils;


public class Pds3InstrumentParser
{
    private XPathExpression xLid;
    private XPathExpression xTitle;

    private XPathExpression xId;
    private XPathExpression xName;
    private XPathExpression xType;
    private XPathExpression xDescr;

    private XPathExpression xInstrumentHostRef;
    
    
    public Pds3InstrumentParser() throws Exception
    {
        XPathFactory xpf = XPathFactory.newInstance();

        xLid = XPathUtils.compileXPath(xpf, "//Identification_Area/logical_identifier");
        xTitle = XPathUtils.compileXPath(xpf, "//Identification_Area/title");

        xId = XPathUtils.compileXPath(xpf, "//Instrument_PDS3/instrument_id");
        xName = XPathUtils.compileXPath(xpf, "//Instrument_PDS3/instrument_name");
        xType = XPathUtils.compileXPath(xpf, "//Instrument_PDS3/instrument_type");
        xDescr = XPathUtils.compileXPath(xpf, "//Instrument_PDS3/instrument_description");
    
        xInstrumentHostRef = XPathUtils.compileXPath(xpf, "//Reference_List/Internal_Reference[reference_type='has_instrument_host']/lidvid_reference");
    }

    
    public Pds3Instrument parse(Document doc) throws Exception
    {
        Pds3Instrument obj = new Pds3Instrument();

        obj.lid = XPathUtils.getStringValue(doc, xLid);
        obj.shortLid = Pds3Utils.getShortLid(obj.lid);
        obj.title = XPathUtils.getStringValue(doc, xTitle);

        obj.id = XPathUtils.getStringValue(doc, xId);
        obj.name = XPathUtils.getStringValue(doc, xName);
        obj.type = XPathUtils.getStringValue(doc, xType);
        obj.description = XPathUtils.getStringValue(doc, xDescr);

        obj.instrumentHostRef = Pds3Utils.getShortLid(XPathUtils.getStringValue(doc, xInstrumentHostRef));
        
        return obj;
    }
}
