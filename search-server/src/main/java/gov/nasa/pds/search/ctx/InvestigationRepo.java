package gov.nasa.pds.search.ctx;

import java.util.HashMap;
import java.util.Map;


public class InvestigationRepo
{
    private static InvestigationRepo instance;
    
    private Map<String, InvestigationInfo> data;
    
    
    private InvestigationRepo()
    {
        data = new HashMap<>();
    }
    
    public static InvestigationRepo getInstance()
    {
        return instance;
    }
    
    
    public static void init()
    {
        instance = new InvestigationRepo();
        instance.data.put("mro", new InvestigationInfo("mars"));
        instance.data.put("lro", new InvestigationInfo("moon"));
    }
    
    
    public InvestigationInfo findById(String id)
    {
        return data.get(id);
    }

}
