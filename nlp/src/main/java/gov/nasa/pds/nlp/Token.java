package gov.nasa.pds.nlp;

public class Token
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
    
    public String text;
    public byte type;
    
    
    public Token(String text, byte type)
    {
        this.text = text;
        this.type = type;
    }
}
