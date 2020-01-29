package gov.nasa.pds.nlp.query;

/**
 * Classes (categories) of context queries.
 * 
 * @author karpenko
 */
public class ContextQueryClass
{
    public static byte UNKNOWN = 0;
    public static byte TARGET = 1;
    public static byte INSTRUMENT = 2;
    public static byte INVESTIGATION = 3;
    
    
    public static String toString(byte cc)
    {
        switch(cc)
        {
        case 1: return "Target";
        case 2: return "Instrument";
        case 3: return "Investigation";
        }
        
        return "Unknown";
    }
}
