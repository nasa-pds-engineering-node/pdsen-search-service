package gov.nasa.pds.data.pds3.model;

import java.util.Set;
import java.util.TreeSet;

public class Pds3Resource
{
    public String lid;
    public String vid;

    public String resourceUrl;
    public String resourceType;
    
    public String investigationName;
    public Set<String> investigationIds = new TreeSet<>();
}
