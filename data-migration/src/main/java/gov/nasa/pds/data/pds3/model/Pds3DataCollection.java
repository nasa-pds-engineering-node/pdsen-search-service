package gov.nasa.pds.data.pds3.model;

import java.util.List;
import java.util.Set;
import java.util.TreeSet;

public class Pds3DataCollection
{
    public String lid;
    public String vid;
    public String datasetId;
    
    public String title;
    public String type;
    public List<String> description;

    public Set<String> processingLevels;
    public Set<String> codmacLevels;
    public Set<String> scienceFacets = new TreeSet<>();
    
    public String[] keywords;
    public String purpose;
    
    public String collectionType;
    
    // References
    public Set<String> investigationIds;
    public Set<String> instrumentHostIds;
    
    public Set<String> instrumentIds;
    public Set<String> instrumentTypes;
    
    public Set<String> targetNames;
    public Set<String> targetTypes;
}

