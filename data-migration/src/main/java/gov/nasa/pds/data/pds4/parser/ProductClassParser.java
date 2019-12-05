package gov.nasa.pds.data.pds4.parser;

import javax.xml.xpath.XPathExpression;
import javax.xml.xpath.XPathFactory;

import org.w3c.dom.Document;

import gov.nasa.pds.data.util.xml.XPathUtils;

public class ProductClassParser
{
    private XPathExpression xInstrument;
    private XPathExpression xInstrumentHost;
    private XPathExpression xInvestigation;
    private XPathExpression xTarget;

    
    public ProductClassParser() throws Exception
    {
        XPathFactory xpf = XPathFactory.newInstance();
        
        xInstrument = XPathUtils.compileXPath(xpf, "/Product_Context/Instrument");
        xInstrumentHost = XPathUtils.compileXPath(xpf, "/Product_Context/Instrument_Host");
        xInvestigation = XPathUtils.compileXPath(xpf, "/Product_Context/Investigation");
        xTarget = XPathUtils.compileXPath(xpf, "/Product_Context/Target");
    }
    
    
    public String getProductClass(Document doc) throws Exception
    {
        if(XPathUtils.exists(doc, xInstrument)) return "Instrument";
        if(XPathUtils.exists(doc, xInstrumentHost)) return "Instrument_Host";
        if(XPathUtils.exists(doc, xInvestigation)) return "Investigation";
        if(XPathUtils.exists(doc, xTarget)) return "Target";
        
        return null;
    }
}
