package tt;

import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

import gov.nasa.pds.data.pds3.tools.SolrDumpProcessor_Pds3DataSet;


public class TestPds3DataDumpProcessor
{

    public static void main(String[] args) throws Exception
    {
        SolrDumpProcessor_Pds3DataSet processor = new SolrDumpProcessor_Pds3DataSet();
        processor.processFile("/tmp/pds3-ds-1.xml", "/tmp/dawn.xml");

        //crawl("/tmp");
    }

    
    public static void crawl(String folderPath) throws Exception
    {
        Path folder = Paths.get(folderPath);
        if(!Files.isDirectory(folder))
        {
            throw new IllegalArgumentException("Not a folder: " + folderPath);
        }

        SolrDumpProcessor_Pds3DataSet processor = new SolrDumpProcessor_Pds3DataSet();
        
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
                    processor.processFile(str, str + ".clean");
                }
                catch(Exception ex)
                {
                    throw new RuntimeException(ex);
                }
            }
        );
    }
}
