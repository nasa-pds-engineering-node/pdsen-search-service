package gov.nasa.pds.search.feature;

import java.util.HashMap;
import java.util.Map;

public final class FeatureRepo
{
    private static FeatureRepo instance;
    
    private Map<String, FeatureInfo> data;
    
    
    private FeatureRepo()
    {
        data = new HashMap<>();
    }
    
    public static FeatureRepo getInstance()
    {
        return instance;
    }
    
    
    public static void init()
    {
        instance = new FeatureRepo();
        instance.data.put("1", new FeatureInfo("gale", "crater"));
    }
    
    
    public FeatureInfo findById(String id)
    {
        return data.get(id);
    }
}
