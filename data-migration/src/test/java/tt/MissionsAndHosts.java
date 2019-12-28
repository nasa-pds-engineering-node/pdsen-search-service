package tt;

import java.io.BufferedReader;
import java.io.FileReader;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

public class MissionsAndHosts
{

    public static void main(String[] args) throws Exception
    {
        Map<String, String> hostName2Index = loadHostMap("/ws/data/NER/ih.n2id");
        List<String> missions = loadMissionNames("/ws/data/NER/mis.pds4");
        
        for(String mis: missions)
        {
            System.out.print(mis + "->T=3");
            String id = hostName2Index.get(mis);
            if(id != null)
            {
                System.out.print("|I=" + id);
            }
            
            System.out.println();
        }
    }

    
    private static List<String> loadMissionNames(String path) throws Exception
    {
        List<String> list = new ArrayList<>();
        
        BufferedReader rd = new BufferedReader(new FileReader(path));

        String line;
        while((line = rd.readLine()) != null)
        {
            line = line.trim();
            if(line.isEmpty()) continue;

            list.add(line);
        }

        rd.close();
        
        Collections.sort(list);
        return list;
    }
    
    
    private static Map<String, String> loadHostMap(String path) throws Exception
    {
        BufferedReader rd = new BufferedReader(new FileReader(path));
        
        Map<String, String> map = new TreeMap<>();
        
        String line;
        while((line = rd.readLine()) != null)
        {
            line = line.trim();
            if(line.isEmpty()) continue;

            String[] tokens = line.split("\\|");
            if(tokens.length != 2) continue;
            
            map.put(tokens[0], tokens[1]);
        }

        rd.close();
        
        return map;
    }
}
