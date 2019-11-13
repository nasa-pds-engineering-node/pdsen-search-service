package gov.nasa.pds.data.util.xml;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;

import org.w3c.dom.Document;


public class XmlDomCrawler 
{
	public static interface DomCallback
	{
		public void onDocument(Document doc) throws Exception;
	}
	
	private Path folder;
	
	
	public XmlDomCrawler(String folderPath)
	{
		folder = Paths.get(folderPath);
		if(!Files.isDirectory(folder))
		{
			throw new IllegalArgumentException("Not a folder: " + folderPath);
		}
	}
	
	
	public void crawl(DomCallback cb) throws IOException
	{
		DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance(); 
		
		Files.walk(folder).filter(p -> p.toString().endsWith(".xml")).forEach(p -> 
		{
			try 
			{
				DocumentBuilder db = dbf.newDocumentBuilder();
				Document doc = db.parse(p.toFile());
				cb.onDocument(doc);
			} 
			catch(Exception ex) 
			{
				ex.printStackTrace();
			}
		});		
	}

}
