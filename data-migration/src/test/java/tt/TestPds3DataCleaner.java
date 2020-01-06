package tt;

import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Map;

import gov.nasa.pds.data.pds3.tools.Pds3DataCleaner;
import gov.nasa.pds.data.util.MapUtils;

public class TestPds3DataCleaner
{

    public static void main(String[] args) throws Exception
    {
        Map<String, String> missionMap = MapUtils.loadMap("src/main/data/pds3/mission_name_to_id.txt");
        Pds3DataCleaner.processFile("/tmp/pds3-ds-2.xml", "/tmp/pds3-ds-2.xml.clean", missionMap);

        //crawl("/tmp");
    }

    
    public static void crawl(String folderPath) throws Exception
    {
        Path folder = Paths.get(folderPath);
        if(!Files.isDirectory(folder))
        {
            throw new IllegalArgumentException("Not a folder: " + folderPath);
        }

        
        Map<String, String> missionMap = MapUtils.loadMap("src/main/data/pds3/mission_name_to_id.txt");
        
        
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
                    Pds3DataCleaner.processFile(str, str + ".clean", missionMap);
                }
                catch(Exception ex)
                {
                    throw new RuntimeException(ex);
                }
            }
        );
    }
}
