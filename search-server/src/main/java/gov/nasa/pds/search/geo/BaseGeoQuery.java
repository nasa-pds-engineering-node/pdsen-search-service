package gov.nasa.pds.search.geo;

public abstract class BaseGeoQuery
{
    public static enum Type { METADATA, LID };
    
    public abstract Type getType();
}
