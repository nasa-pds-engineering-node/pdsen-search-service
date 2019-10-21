package tt;

import java.io.BufferedReader;
import java.io.FileReader;

import gov.nasa.pds.nlp.MultiWordDictionary;
import gov.nasa.pds.nlp.NamedEntityRecognizer;

public class TestNER
{

    public static void main(String[] args) throws Exception
    {
        MultiWordDictionary dic = new MultiWordDictionary();        
        loadDictionary(dic, "src/test/data/ner-dic.txt");
        
        NamedEntityRecognizer ner = new NamedEntityRecognizer(dic);
        
        String sent1 = "MRO CRISM TRDRs over Gale crater on Mars"; 
        String sent2 = "Mars Reconnaissance Orbiter CRISM TRDRs over Gale crater on Mars";
        String sent3 = "Mars Science Laboratory";
        String sent4 = "Mars Science";
        
        ner.parse(sent3);
    }
    
    
    private static void loadDictionary(MultiWordDictionary dic, String file) throws Exception
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
            String type = tokens[1];
            String id = tokens[2];
            
            dic.add(name, type, id);
        }
        
        rd.close();        
    }

}
