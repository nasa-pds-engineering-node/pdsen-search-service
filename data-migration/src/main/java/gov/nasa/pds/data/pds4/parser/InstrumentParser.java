package gov.nasa.pds.data.pds4.parser;

import javax.xml.xpath.XPathExpression;
import javax.xml.xpath.XPathFactory;

import org.apache.commons.lang.StringUtils;
import org.w3c.dom.Document;

import gov.nasa.pds.data.pds3.parser.Pds3Utils;
import gov.nasa.pds.data.pds4.model.Instrument;
import gov.nasa.pds.data.util.xml.XPathUtils;


public class InstrumentParser
{
    private XPathExpression xLid;
    private XPathExpression xVid;

    private XPathExpression xName;
    private XPathExpression xType;
    private XPathExpression xDescr;
    
    private XPathExpression xInstrumentHostRef;
    private XPathExpression xInstrumentHostRef2;

    
    public InstrumentParser() throws Exception
    {
        XPathFactory xpf = XPathFactory.newInstance();

        xLid = XPathUtils.compileXPath(xpf, "//Identification_Area/logical_identifier");
        xVid = XPathUtils.compileXPath(xpf, "//Identification_Area/version_id");
        
        xName = XPathUtils.compileXPath(xpf, "//Instrument/name");
        xType = XPathUtils.compileXPath(xpf, "//Instrument/type");
        xDescr = XPathUtils.compileXPath(xpf, "//Instrument/description");
        
        xInstrumentHostRef = XPathUtils.compileXPath(xpf, "//Reference_List/Internal_Reference[reference_type='instrument_to_instrument_host']/lid_reference");
        xInstrumentHostRef2 = XPathUtils.compileXPath(xpf, "//Reference_List/Internal_Reference[reference_type='instrument_to_instrument_host']/lidvid_reference");
    }
    
    
    public Instrument parse(Document doc) throws Exception
    {
        Instrument obj = new Instrument();

        obj.lid = XPathUtils.getStringValue(doc, xLid);
        obj.shortLid = Pds3Utils.getShortLid(obj.lid);
        
        String strVid = XPathUtils.getStringValue(doc, xVid); 
        obj.vid = Float.parseFloat(strVid);

        obj.id = extractInstrumentId(obj.shortLid);
        obj.name = StringUtils.normalizeSpace(XPathUtils.getStringValue(doc, xName));
        obj.type = XPathUtils.getStringValue(doc, xType);
        obj.description = XPathUtils.getStringValue(doc, xDescr);

        // References
        obj.instrumentHostRef = XPathUtils.getStringValue(doc, xInstrumentHostRef);
        if(obj.instrumentHostRef == null || obj.instrumentHostRef.isEmpty())
        {
            // lidvid reference
            obj.instrumentHostRef = XPathUtils.getStringValue(doc, xInstrumentHostRef2);
        }
        
        obj.instrumentHostId = ParserUtils.getShortLid(obj.instrumentHostRef);
        if(obj.instrumentHostId != null && obj.instrumentHostId.startsWith("spacecraft."))
        {
            obj.instrumentHostId = obj.instrumentHostId.substring(11);
        }
        
        return obj;
    }
    
    
    private static String extractInstrumentId(String shortLid)
    {
        if(shortLid == null) return null;
        if(shortLid.startsWith("instrument.")) shortLid = shortLid.substring(11);
        
        if(shortLid.startsWith("dawn."))
        {
            return shortLid.substring(5);
        }

        if(shortLid.startsWith("vex."))
        {
            return shortLid.substring(4);
        }

        int idx = shortLid.indexOf(".");
        if(idx > 0) return shortLid.substring(0, idx);
        
        idx = shortLid.indexOf("__");
        if(idx > 0) return shortLid.substring(0, idx);
        
        return null;
    }
}
