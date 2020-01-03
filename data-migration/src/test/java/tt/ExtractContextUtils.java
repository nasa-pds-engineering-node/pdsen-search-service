package tt;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.util.HashSet;
import java.util.Set;

public class ExtractContextUtils
{
    public static Set<String> readLatestIndex(String dir) throws Exception
    {
        File file = new File(dir, "latest.idx");
        if(!file.exists()) throw new RuntimeException("latest.xml index file doesn't exist in " + dir);
        
        BufferedReader rd = new BufferedReader(new FileReader(file));
        
        Set<String> set = new HashSet<>();
        
        String line;
        while((line = rd.readLine()) != null)
        {
            line = line.trim();
            if(line.isEmpty()) continue;
            
            set.add(line);
        }
        
        rd.close();
        
        return set;
    }    

}
