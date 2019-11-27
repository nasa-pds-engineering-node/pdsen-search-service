package tt;

import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

import gov.nasa.pds.data.pds3.tools.Pds3DataCleaner;

public class TestPds3DataCleaner
{

    public static void main(String[] args) throws Exception
    {        
        //Pds3DataCleaner.processFile("/tmp/pds3-ds-11.xml", "/tmp/pds3-ds-11.xml.clean");
        
        crawl("/tmp");
    }

    
    public static void crawl(String folderPath) throws Exception
    {
        Path folder = Paths.get(folderPath);
        if(!Files.isDirectory(folder))
        {
            throw new IllegalArgumentException("Not a folder: " + folderPath);
        }

        Files.list(folder).filter(p -> 
            {
                String str = p.getFileName().toString();
                return str.startsWith("pds3-ds") && str.endsWith(".xml"); 
            }
        ).forEach(p -> 
            {
                String str = p.toString();
                try
                {
                    System.out.println("Processing: " + str);
                    Pds3DataCleaner.processFile(str, str + ".clean");
                }
                catch(Exception ex)
                {
                    throw new RuntimeException(ex);
                }
            }
        );
    }
}
