package gov.nasa.pds.nlp.ner;

public class NerToken
{
    public static final byte TYPE_UNKNOWN = 0;
    
    public static final byte TYPE_TARGET = 1;
    public static final byte TYPE_INSTRUMENT = 2;
    public static final byte TYPE_INSTRUMENT_HOST = 3;
    public static final byte TYPE_INVESTIGATION = 4;
    public static final byte TYPE_INVESTIGATION_AND_HOST = 5;
    
    public static final byte TYPE_PRODUCT_TYPE = 7;

    public static final byte TYPE_FEATURE = 9;    

    public static final byte TYPE_LID = 20;
    
    public String key;
    public String id;
    public int type;
    public boolean hasNext;
    
    
    public NerToken(String key)
    {
        this(key, 0);
    }

    
    public NerToken(String key, int type)
    {
        this.key = key;
        this.type = type;
        hasNext = false;
    }

}
