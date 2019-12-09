package gov.nasa.pds.data.pds3.parser;

import javax.xml.xpath.XPathExpression;
import javax.xml.xpath.XPathFactory;
import org.w3c.dom.Document;

import gov.nasa.pds.data.pds3.model.Pds3InstrumentHost;
import gov.nasa.pds.data.util.xml.XPathUtils;


public class Pds3InstrumentHostParser
{
    private XPathExpression xLid;
    private XPathExpression xTitle;

    private XPathExpression xId;
    private XPathExpression xName;
    private XPathExpression xType;
    private XPathExpression xDescr;
    
    private XPathExpression xInvestigationRef;
    private XPathExpression xInstrumentRef;
    private XPathExpression xTargetRef;

    
    public Pds3InstrumentHostParser() throws Exception
    {
        XPathFactory xpf = XPathFactory.newInstance();

        xLid = XPathUtils.compileXPath(xpf, "//Identification_Area/logical_identifier");
        xTitle = XPathUtils.compileXPath(xpf, "//Identification_Area/title");

        xId = XPathUtils.compileXPath(xpf, "//Instrument_Host_PDS3/instrument_host_id");
        xName = XPathUtils.compileXPath(xpf, "//Instrument_Host_PDS3/instrument_host_name");
        xType = XPathUtils.compileXPath(xpf, "//Instrument_Host_PDS3/instrument_host_type");
        xDescr = XPathUtils.compileXPath(xpf, "//Instrument_Host_PDS3/instrument_host_desc");
        
        xInvestigationRef = XPathUtils.compileXPath(xpf, "//Reference_List/Internal_Reference[reference_type='has_mission']/lidvid_reference");
        xInstrumentRef = XPathUtils.compileXPath(xpf, "//Reference_List/Internal_Reference[reference_type='has_instrument']/lidvid_reference");
        xTargetRef = XPathUtils.compileXPath(xpf, "//Reference_List/Internal_Reference[reference_type='has_target']/lidvid_reference");

    }

    
    public Pds3InstrumentHost parse(Document doc) throws Exception
    {
        Pds3InstrumentHost ih = new Pds3InstrumentHost();

        ih.lid = XPathUtils.getStringValue(doc, xLid);
        ih.shortLid = Pds3Utils.getShortLid(ih.lid);
        ih.title = XPathUtils.getStringValue(doc, xTitle);

        ih.id = XPathUtils.getStringValue(doc, xId);
        ih.name = XPathUtils.getStringValue(doc, xName);
        ih.type = XPathUtils.getStringValue(doc, xType);
        ih.description = XPathUtils.getStringValue(doc, xDescr);

        // References
        ih.investigationRef = XPathUtils.getStringArray(doc, xInvestigationRef);
        ih.instrumentRef = XPathUtils.getStringArray(doc, xInstrumentRef);
        ih.targetRef = XPathUtils.getStringArray(doc, xTargetRef);

        Pds3Utils.toShortLid(ih.investigationRef);
        Pds3Utils.toShortLid(ih.instrumentRef);
        Pds3Utils.toShortLid(ih.targetRef);
        
        return ih;
    }    
    
}
