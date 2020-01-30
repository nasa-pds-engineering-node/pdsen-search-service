package gov.nasa.pds.nlp.query;

/**
 * Classes (categories) of context queries.
 * 
 * @author karpenko
 */
public class ContextQueryClass
{
    public static final byte UNKNOWN = 0;
    public static final byte TARGET = 1;
    public static final byte INSTRUMENT = 2;
    public static final byte INVESTIGATION = 3;
    
    
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
