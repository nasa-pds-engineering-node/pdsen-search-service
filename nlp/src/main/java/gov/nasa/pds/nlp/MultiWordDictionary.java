package gov.nasa.pds.nlp;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.util.Map;
import java.util.TreeMap;

public class MultiWordDictionary
{
    private Map<String, SingleWordTermInfo> singleWords;
    private MultiWordTermInfo multiWords;
    
    
    public MultiWordDictionary()
    {
        singleWords = new TreeMap<>();
        multiWords = new MultiWordTermInfo();
    }
    
    
    public void add(String name, byte type, String id)
    {
        name = name.toLowerCase();
    
        // Single word
        if(name.indexOf(' ') < 0)
        {
            singleWords.put(name, new SingleWordTermInfo(id, type));
        }
        // Multiple words
        else
        {
            String[] tokens = name.split(" ");
            multiWords.add(tokens, type, id);
        }
    }
    
    
    public MultiWordTermInfo findMultiWordTerm(String firstWord)
    {
        return multiWords.getTermInfo(firstWord);
    }
    
    
    public SingleWordTermInfo findSingleWordTerm(String word)
    {
        return singleWords.get(word);
    }
    
    
    public void load(File file) throws Exception
    {
        BufferedReader rd = new BufferedReader(new FileReader(file));
        
        String line;
        while((line = rd.readLine()) != null)
        {
            line = line.trim();
            // Skip comments and empty lines
            if(line.startsWith("#") || line.isEmpty()) continue;
            
            String[] tokens = line.split("\\|");
            if(tokens.length != 3)
            {
                System.out.println("WARNING: Invalid entry: " + line);
                continue;
            }
            
            String name = tokens[0];
            byte type = Byte.parseByte(tokens[1]);
            String id = tokens[2];
            
            add(name, type, id);
        }
        
        rd.close();        
    }

}
