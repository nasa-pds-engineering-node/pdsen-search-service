package gov.nasa.pds.data.pds4.model;

public class ProductCollection
{
    public String lid;
    public float vid;
    
    public String title;
    public String type;
    public String[] description;

    public String processingLevel;
    public String[] scienceFacets;
    
    public String[] keywords;
    public String purpose;
    
    // References
    public String[] investigationRef;
    public String[] instrumentHostRef;
    public String[] instrumentRef;
    public String[] targetRef;
}
