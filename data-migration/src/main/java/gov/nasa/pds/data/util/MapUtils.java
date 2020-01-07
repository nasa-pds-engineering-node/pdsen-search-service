package gov.nasa.pds.data.util;

import java.io.BufferedReader;
import java.io.FileReader;
import java.util.HashMap;
import java.util.Map;

public class MapUtils
{
    public static Map<String, String> loadMap(String path) throws Exception
    {
        BufferedReader rd = new BufferedReader(new FileReader(path));
        
        Map<String, String> map = new HashMap<>();
        
        String line;
        while((line = rd.readLine()) != null)
        {
            line = line.trim();
            if(line.isEmpty() || line.startsWith("#")) continue;
            
            String tokens[] = line.split("\\|");
            map.put(tokens[0], tokens[1]);
        }
        
        rd.close();
        
        return map;
    }    

}
