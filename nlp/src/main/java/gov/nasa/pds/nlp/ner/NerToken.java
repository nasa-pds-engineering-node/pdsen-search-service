package gov.nasa.pds.nlp.ner;

public class NerToken
{
    public static final byte TYPE_UNKNOWN = 0;
    public static final byte TYPE_MULTIPLE = -1;
    
    public static final byte TYPE_TARGET = 1;
    public static final byte TYPE_INSTRUMENT = 2;
    public static final byte TYPE_INVESTIGATION = 3;
    public static final byte TYPE_INSTRUMENT_HOST = 4;
    
    public static final byte TYPE_PRODUCT_TYPE = 7;
    public static final byte TYPE_FEATURE = 9;    

    public static final byte TYPE_LID = 20;
    
    private String key;
    private String id;
    
    private int type;
    private int[] types;
    
    private boolean hasNext = false;
    
    
    public NerToken(String key)
    {
        this.key = key;
    }

    
    public NerToken(String key, int type)
    {
        this.key = key;
        this.type = type;
    }

    
    public int getType()
    {
        return type;
    }
    
    
    public int[] getAllTypes()
    {
        return types;
    }
    
    
    public void setType(int type)
    {
        this.type = type;
    }
    
    
    public void setTypes(int[] types)
    {
        if(types == null || types.length == 0) 
        {
            throw new IllegalArgumentException("Types array is null or empty");
        }
        
        if(types.length == 1)
        {
            this.type = types[0];
        }
        else
        {
            this.type = TYPE_MULTIPLE;
            this.types = types;
        }
    }

    
    public String getKey()
    {
        return key;
    }
    
    
    public String getId()
    {
        return id;
    }
    
    
    public void setId(String id)
    {
        this.id = id;
    }
    
    
    public boolean hasNext()
    {
        return hasNext;
    }
    
    
    public void setHasNext(boolean b)
    {
        this.hasNext = b;
    }
}
