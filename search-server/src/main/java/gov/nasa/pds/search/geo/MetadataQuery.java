package gov.nasa.pds.search.geo;

public class MetadataQuery extends BaseGeoQuery
{
    public MetadataQuery()
    {
    }

    
    @Override
    public Type getType()
    {
        return Type.METADATA;
    }

}
