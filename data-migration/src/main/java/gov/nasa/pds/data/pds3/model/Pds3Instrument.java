package gov.nasa.pds.data.pds3.model;

import java.util.Set;
import java.util.TreeSet;

public class Pds3Instrument
{
    public String lid;
    public String shortLid;
    
    public String title;

    public String id;
    public String name;
    public String type;
    public String description;
    
    public Set<String> instrumentTypes = new TreeSet<String>();
    
    public String instrumentHostRef;
    public String instrumentHostId;
}
