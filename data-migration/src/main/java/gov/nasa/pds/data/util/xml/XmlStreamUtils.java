package gov.nasa.pds.data.util.xml;

import javax.xml.stream.XMLEventReader;
import javax.xml.stream.events.XMLEvent;


public class XmlStreamUtils 
{
	public static boolean goToTag(XMLEventReader reader, String tag) throws Exception
	{
		while(reader.hasNext())
		{
			XMLEvent event = reader.nextEvent();
			if(event.isStartElement() && event.asStartElement().getName().getLocalPart().equals(tag))
			{
				return true;
			}
		}

		return false;
	}
	

}
