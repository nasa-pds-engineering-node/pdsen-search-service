package gov.nasa.pds.data.util;

import java.io.BufferedReader;
import java.io.FileReader;
import java.util.Map;


public class MapUtils
{
    public static void loadMap(Map<String, String> map, String path) throws Exception
    {
        if(map == null) return;
        
        BufferedReader rd = new BufferedReader(new FileReader(path));
        
        String line;
        while((line = rd.readLine()) != null)
        {
            line = line.trim();
            if(line.isEmpty() || line.startsWith("#")) continue;
            
            String tokens[] = line.split("\\|");
            map.put(tokens[0], tokens[1]);
        }
        
        rd.close();
    }    

}
