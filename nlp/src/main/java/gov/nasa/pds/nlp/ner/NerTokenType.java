package gov.nasa.pds.nlp.ner;

public interface NerTokenType
{
    public static final byte UNKNOWN = 0;
    public static final byte MULTIPLE = -1;
    
    public static final byte TARGET = 1;
    public static final byte INSTRUMENT = 2;
    public static final byte INVESTIGATION = 3;
    public static final byte INSTRUMENT_HOST = 4;
    
    public static final byte TARGET_TYPE = 10;    

    public static final byte LID = 20;
}
