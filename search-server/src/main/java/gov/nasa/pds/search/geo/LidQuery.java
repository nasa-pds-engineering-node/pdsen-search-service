package gov.nasa.pds.search.geo;

public class LidQuery extends BaseGeoQuery
{
    public String lid;
    public boolean listFiles = false;
    
    public LidQuery(String lid)
    {
        this.lid = lid;
    }
    
    
    @Override
    public Type getType()
    {
        return Type.LID;
    }

}
