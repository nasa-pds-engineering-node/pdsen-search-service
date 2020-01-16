package gov.nasa.pds.data.pds3.model;

public class Pds3DataCollection
{
    public String lid;
    public String vid;
    public String datasetId;
    
    public String title;
    public String type;
    public String[] description;

    public String processingLevel;
    public String[] scienceFacets;
    
    public String[] keywords;
    public String purpose;
    
    // References
    public String[] investigationId;
    public String[] instrumentHostId;
    public String[] instrumentId;
    public String[] targetName;
    public String[] targetType;
}
