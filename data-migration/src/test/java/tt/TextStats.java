package tt;

import java.io.BufferedReader;
import java.io.FileReader;
import java.util.List;
import java.util.Set;
import java.util.TreeSet;

import gov.nasa.pds.nlp.lex.PdsLexer;

public class TextStats
{

    public static void main(String[] args) throws Exception
    {
        String textFile = "/tmp/ds1.txt";
        
        
        Set<String> stopWords = new TreeSet<>(); 
        loadWords(stopWords, "src/main/data/pds3/stop-words-1.txt");
        loadWords(stopWords, "src/main/data/pds3/stop-words.txt");
        
        // Temporary
        loadWords(stopWords, "src/main/data/pds3/keywords.txt");
        
        
        Set<String> words = new TreeSet<>();
                
        BufferedReader rd = new BufferedReader(new FileReader(textFile));
        
        PdsLexer lexer = new PdsLexer();
        
        String line;
        while((line = rd.readLine()) != null)
        {
            List<String> tokens = lexer.parse(line);
            
            for(String token: tokens)
            {
                if(token.length() < 4) continue;                
                if(Character.isDigit(token.charAt(0)) || Character.isDigit(token.charAt(1))) continue;
                
                if(token.startsWith("a-")) continue;
                if(token.startsWith("mtp0")) continue;
                if(token.startsWith("ballardeta")) continue;
                
                
                
                if(stopWords.contains(token)) continue;
                
                words.add(token);
            }
            
            
        }
        
        rd.close();
        
        int count = 0;
        for(String str: words)
        {
            count++;
            System.out.println(str);
            if(count > 200) break;
        }
    }

    
    public static void loadWords(Set<String> set, String file) throws Exception
    {
        BufferedReader rd = new BufferedReader(new FileReader(file));
        
        String line;
        while((line = rd.readLine()) != null)
        {
            line = line.trim();
            if(line.isEmpty() || line.startsWith("#")) continue;
            
            set.add(line);
        }        
        
        rd.close();
    }
}
