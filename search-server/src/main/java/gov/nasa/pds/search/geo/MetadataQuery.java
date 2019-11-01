package gov.nasa.pds.search.geo;

public class MetadataQuery extends BaseGeoQuery
{
    public String targetId;
    public String missionId;
    public String instrumentId;    
    public String productType;
    public String featureName;
    public String featureType;
    
    
    public MetadataQuery()
    {
    }

    
    @Override
    public Type getType()
    {
        return Type.METADATA;
    }

}
