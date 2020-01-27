package gov.nasa.pds.data.pds4.model;

import java.util.Set;
import java.util.TreeSet;

public class Investigation
{
    public String lid;
    public String shortLid;
    public String[] id;
    public String vid;

    public String title;
    public String name;
    public String type;
    public String description;
    
    public String[] hostIds;
    public Set<String> targetIds;
    public Set<String> targetTypes;
    
    
    public void initTargets()
    {
        targetIds = new TreeSet<>();
        targetTypes = new TreeSet<>();
    }
}
