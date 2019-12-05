package gov.nasa.pds.data.pds3.parser;

import javax.xml.xpath.XPathExpression;
import javax.xml.xpath.XPathFactory;
import org.w3c.dom.Document;

import gov.nasa.pds.data.pds3.model.Pds3InstrumentHost;
import gov.nasa.pds.data.util.xml.XPathUtils;
import gov.nasa.pds.data.util.xml.XmlDomCrawler;


public class Pds3InstrumentHostParser
{
    private XPathExpression xLid;
    private XPathExpression xTitle;

    private XPathExpression xId;
    private XPathExpression xName;
    private XPathExpression xType;
    private XPathExpression xDescr;

    
    public Pds3InstrumentHostParser() throws Exception
    {
        XPathFactory xpf = XPathFactory.newInstance();

        xLid = XPathUtils.compileXPath(xpf, "//Identification_Area/logical_identifier");
        xTitle = XPathUtils.compileXPath(xpf, "//Identification_Area/title");

        xId = XPathUtils.compileXPath(xpf, "//Instrument_Host_PDS3/instrument_host_id");
        xName = XPathUtils.compileXPath(xpf, "//Instrument_Host_PDS3/instrument_host_name");
        xType = XPathUtils.compileXPath(xpf, "//Instrument_Host_PDS3/instrument_host_type");
        xDescr = XPathUtils.compileXPath(xpf, "//Instrument_Host_PDS3/instrument_host_desc");
    }

    
    public Pds3InstrumentHost parse(Document doc) throws Exception
    {
        Pds3InstrumentHost obj = new Pds3InstrumentHost();

        obj.lid = XPathUtils.getStringValue(doc, xLid);
        obj.title = XPathUtils.getStringValue(doc, xTitle);

        obj.id = XPathUtils.getStringValue(doc, xId);
        obj.name = XPathUtils.getStringValue(doc, xName);
        obj.type = XPathUtils.getStringValue(doc, xType);
        obj.description = XPathUtils.getStringValue(doc, xDescr);

        return obj;
    }
    
    
    public static interface Callback
    {
        public void onInstrumentHost(Pds3InstrumentHost ih) throws Exception;
    }

    
    public static void crawl(String directory, Callback cb) throws Exception
    {
        XmlDomCrawler crawler = new XmlDomCrawler(directory);
        Pds3InstrumentHostParser parser = new Pds3InstrumentHostParser();
        
        crawler.crawl((doc, path) -> 
        {
            Pds3InstrumentHost ih = parser.parse(doc);
            cb.onInstrumentHost(ih);
        });
    }

}
