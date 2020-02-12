package tt;

import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.HashMap;
import java.util.Map;

import gov.nasa.pds.data.pds3.tools.Pds3InstrumentCleaner;
import gov.nasa.pds.data.util.MapUtils;


public class TestPds3InstrumentCleaner
{
    private static final String HOST_2_MISSION_DIC = "src/main/data/pds3/host2mission.ids";
    
    
    public static void main(String[] args) throws Exception
    {
        // Read host id to mission id map
        Map<String, String> host2mis = new HashMap<>(100); 
        MapUtils.loadMap(host2mis, HOST_2_MISSION_DIC);

        Pds3InstrumentCleaner.processFile("/tmp/instruments_pds3-0.xml", "/tmp/instruments_pds3-0.xml.clean", host2mis);

        //crawl("/tmp");
    }

    
    public static void crawl(String folderPath) throws Exception
    {
        Path folder = Paths.get(folderPath);
        if(!Files.isDirectory(folder))
        {
            throw new IllegalArgumentException("Not a folder: " + folderPath);
        }
        
        Map<String, String> host2mis = new HashMap<>(100); 
        MapUtils.loadMap(host2mis, HOST_2_MISSION_DIC);
                
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
                    Pds3InstrumentCleaner.processFile(str, str + ".clean", host2mis);
                }
                catch(Exception ex)
                {
                    throw new RuntimeException(ex);
                }
            }
        );
    }
}
