package gov.nasa.pds.data.pds4.parser;

import javax.xml.xpath.XPathExpression;
import javax.xml.xpath.XPathFactory;

import org.w3c.dom.Document;

import gov.nasa.pds.data.pds4.model.InstrumentHost;
import gov.nasa.pds.data.util.xml.XPathUtils;


public class InstrumentHostParser
{
    private XPathExpression xLid;
    private XPathExpression xVid;

    private XPathExpression xName;
    private XPathExpression xType;
    private XPathExpression xDescr;

    private XPathExpression xNaifHostId;
    
    private XPathExpression xInvestigationRef;
    private XPathExpression xInstrumentRef;
    private XPathExpression xTargetRef;

    
    public InstrumentHostParser() throws Exception
    {
        XPathFactory xpf = XPathFactory.newInstance();

        xLid = XPathUtils.compileXPath(xpf, "//Identification_Area/logical_identifier");
        xVid = XPathUtils.compileXPath(xpf, "//Identification_Area/version_id");
        
        xName = XPathUtils.compileXPath(xpf, "//Instrument_Host/name");
        xType = XPathUtils.compileXPath(xpf, "//Instrument_Host/type");
        xDescr = XPathUtils.compileXPath(xpf, "//Instrument_Host/description");
        xNaifHostId = XPathUtils.compileXPath(xpf, "//Instrument_Host/naif_host_id");
        
        xInvestigationRef = XPathUtils.compileXPath(xpf, "//Reference_List/Internal_Reference[reference_type='instrument_host_to_investigation']/lid_reference");
        xInstrumentRef = XPathUtils.compileXPath(xpf, "//Reference_List/Internal_Reference[reference_type='instrument_host_to_instrument']/lid_reference");
        xTargetRef = XPathUtils.compileXPath(xpf, "//Reference_List/Internal_Reference[reference_type='instrument_host_to_target']/lid_reference");
    }
    
    
    public InstrumentHost parse(Document doc) throws Exception
    {
        InstrumentHost obj = new InstrumentHost();

        obj.lid = XPathUtils.getStringValue(doc, xLid);
        String strVid = XPathUtils.getStringValue(doc, xVid); 
        obj.vid = Float.parseFloat(strVid);

        obj.id = XPathUtils.getStringValue(doc, xNaifHostId);
        obj.name = XPathUtils.getStringValue(doc, xName);
        obj.type = XPathUtils.getStringValue(doc, xType);
        obj.description = XPathUtils.getStringValue(doc, xDescr);

        // References
        obj.investigationRef = XPathUtils.getStringArray(doc, xInvestigationRef);
        obj.instrumentRef = XPathUtils.getStringArray(doc, xInstrumentRef);
        obj.targetRef = XPathUtils.getStringArray(doc, xTargetRef);
        
        return obj;
    }
    
}
