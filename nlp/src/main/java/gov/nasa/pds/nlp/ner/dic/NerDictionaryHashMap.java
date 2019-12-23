package gov.nasa.pds.nlp.ner.dic;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.nio.file.Files;
import java.util.HashMap;
import java.util.Map;
import java.util.logging.Logger;

import gov.nasa.pds.nlp.ner.NerToken;


public class NerDictionaryHashMap implements NerDictionary
{
    private static final Logger LOG = Logger.getLogger(NerDictionaryHashMap.class.getName());
            
    private Map<String, String> map;
    
    
    public NerDictionaryHashMap()
    {
        this(10_000);
    }
    
    
    public NerDictionaryHashMap(int initialCapacity)
    {
        map = new HashMap<>();
    }


    @Override
    public NerToken get(String key)
    {
        String val = map.get(key);
        if(val == null) return null;
        
        NerToken token = new NerToken(key); 
        
        String[] kvs = val.split("\\|");
        for(String kv: kvs)
        {
            if(kv.equals("N=1"))
            {
                token.hasNext = true;
            }
            else if(kv.startsWith("T="))
            {
                token.type = Integer.parseInt(kv.substring(2)); 
            }
            else if(kv.startsWith("ID="))
            {
                token.id = kv.substring(3);
            }
        }
        
        return token;
    }


    @Override
    public void load(File dir)
    {
        if(!dir.isDirectory())
        {
            throw new IllegalArgumentException("Not a directory: " + dir);
        }

        try
        {
            Files.walk(dir.toPath()).filter(p -> p.toString().endsWith(".dic")).forEach(p -> 
            {
                loadFile(p.toFile());
            });
        }
        catch(RuntimeException ex)
        {
            throw ex;
        }
        catch(Exception ex)
        {
            throw new RuntimeException(ex);
        }
    }
    
    
    private void loadFile(File file)
    {
        LOG.info("Loading NER dictionary from " + file.getAbsolutePath());
        
        try
        {
            BufferedReader rd = new BufferedReader(new FileReader(file));
            
            String line;
            while((line = rd.readLine()) != null)
            {
                line = line.trim();
                if(line.isEmpty() || line.startsWith("#")) continue;
                
                int idx = line.indexOf("->");
                if(idx < 0)
                {
                    LOG.warning("Invalid entry: " + line);
                }
                
                String key = line.substring(0, idx);
                String val = line.substring(idx + 2);
                
                map.put(key, val);
            }

            rd.close();
        }
        catch(Exception ex)
        {
            throw new RuntimeException(ex);
        }
    }
}
