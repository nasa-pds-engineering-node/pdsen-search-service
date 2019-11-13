package gov.nasa.pds.data.util.xml;

import java.io.FileReader;

import javax.xml.namespace.QName;
import javax.xml.stream.XMLEventReader;
import javax.xml.stream.XMLInputFactory;
import javax.xml.stream.events.Attribute;
import javax.xml.stream.events.StartElement;
import javax.xml.stream.events.XMLEvent;


public class SolrDocParser
{
    public static interface Callback 
    {
        public void onDocStart();
        public boolean onDocEnd();
        public void onField(String name, String value);
    }
    
    
    private static final QName ATTR_NAME = new QName("name"); 
    private XMLEventReader reader;
    private Callback cb;
    
    
    public SolrDocParser(String filePath, Callback cb) throws Exception
    {
        this.cb = cb;
        XMLInputFactory fac = XMLInputFactory.newFactory();
        reader = fac.createXMLEventReader(new FileReader(filePath));
    }

    
    public void parse() throws Exception
    {
        while(nextDoc());
    }
    
    
    public void close()
    {
        try 
        {
            reader.close();
        }
        catch(Exception ex)
        {
            // Ignore
        }
    }
    
    
    private boolean nextDoc() throws Exception
    {
        if(XmlStreamUtils.goToTag(reader, "doc") == false) return false;
        
        cb.onDocStart();
        
        return parseDoc();
    }
    
    
    private boolean parseDoc() throws Exception
    {
        while(reader.hasNext())
        {
            XMLEvent event = reader.nextEvent();
            if(event.isEndElement() && event.asEndElement().getName().getLocalPart().equals("doc"))
            {
                return cb.onDocEnd();
            }
            
            if(event.isStartElement())
            {
                StartElement el = event.asStartElement();
                String elName = el.getName().getLocalPart();
                if(elName.equals("field"))
                {
                    Attribute attr = el.getAttributeByName(ATTR_NAME);
                    String fieldName = attr.getValue();
                    String fieldValue = reader.getElementText().trim();
                    
                    cb.onField(fieldName, fieldValue);
                }
            }
        }

        return false;
    }

}
