package gov.nasa.pds.search.geo;

public class LidQuery extends BaseGeoQuery
{
    public LidQuery()
    {        
    }
    
    
    @Override
    public Type getType()
    {
        return Type.LID;
    }

}
