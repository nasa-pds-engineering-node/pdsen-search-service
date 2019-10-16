package gov.nasa.pds.search.util;

import java.util.HashMap;
import java.util.Map;

/**
 * Maps public and internal names.
 * @author karpenko
 */
public class NameMapper
{
    private Map<String, String> pub2int;
    private Map<String, String> int2pub;
    
    
    /**
     * Constructor
     */
    public NameMapper()
    {
        pub2int = new HashMap<>();
        int2pub = new HashMap<>();
    }
    
    
    public void addPublicAndInternal(String publicName, String internalName)
    {
        pub2int.put(publicName, internalName);
        int2pub.put(internalName, publicName);
    }
    
    
    public String findPublicByInternal(String internalName)
    {
        String publicName = int2pub.get(internalName);
        return (publicName == null) ? internalName : publicName;
    }


    public String findInternalByPublic(String publicName)
    {
        String internalName = pub2int.get(publicName);
        return (internalName == null) ? publicName : internalName;
    }
}
