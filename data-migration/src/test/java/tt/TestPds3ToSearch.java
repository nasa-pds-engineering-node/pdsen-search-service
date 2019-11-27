package tt;

import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

import gov.nasa.pds.data.pds3.tools.Pds3ToSearch;


public class TestPds3ToSearch
{

    public static void main(String[] args) throws Exception
    {        
        //Pds3ToSearch.processFile("/tmp/pds3-ds-1.xml.clean", "/tmp/search/pds3-ds-1.xml");
        
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
                return str.startsWith("pds3-ds") && str.endsWith(".xml.clean"); 
            }
        ).forEach(p -> 
            {
                String inPath = p.toString();
                String fileName = p.getFileName().toString(); 
                try
                {
                    System.out.println("Processing: " + inPath);
                    String outPath = "/tmp/search/" + fileName.substring(0, fileName.length() - 6);
                    Pds3ToSearch.processFile(inPath, outPath);
                }
                catch(Exception ex)
                {
                    throw new RuntimeException(ex);
                }
            }
        );
    }
}
