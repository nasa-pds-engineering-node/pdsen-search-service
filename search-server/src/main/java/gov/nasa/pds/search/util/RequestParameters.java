package gov.nasa.pds.search.util;

import java.util.Collection;
import java.util.Map;

public class RequestParameters
{
    private Map<String, String[]> params;
    
    public RequestParameters(Map<String, String[]> params)
    {
        this.params = params;
    }
    
    public String[] getParameterValues(String name)
    {
        return params.get(name);
    }
    

    public String getParameter(String name)
    {
        String[] values = getParameterValues(name);
        
        if(values == null || values.length == 0) 
        {
            return null;
        }
        else
        {
            return values[0];
        }
    }
    
    
    public Integer getIntParameter(String name)
    {
        String val = getParameter(name);
        if(val == null) return null;
        
        try
        {
            return Integer.valueOf(val);
        }
        catch(Exception ex)
        {
            return null;
        }
    }

    
    public Integer getIntParameter(String name, int defaultValue)
    {
        Integer value = getIntParameter(name);
        return (value == null) ? defaultValue : value;
    }

    
    public int size()
    {
        return params.size();
    }
    
    
    public Collection<String> getParameterNames()
    {
        return params.keySet();
    }
}
