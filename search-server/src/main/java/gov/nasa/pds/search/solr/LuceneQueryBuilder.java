package gov.nasa.pds.search.solr;

import java.util.List;

/**
 * Builds a Lucene query string.
 * @author karpenko
 */
public class LuceneQueryBuilder
{
    private StringBuilder bld; 
    
    /**
     * Constructor.
     */
    public LuceneQueryBuilder()
    {
        bld = new StringBuilder();
    }

    
    /**
     * Add query field.
     * @param field
     * @param values
     */
    public void addRequiredField(String field, String value)
    {
        if(value == null) return;
        
        addSpace();
        bld.append("+");
        addTerm(field, value);
    }
    
    
    public void addRequiredField(String fieldName, List<String> values)
    {
        if(values == null || values.size() == 0) return;
        
        addSpace();
        bld.append("+");
        bld.append(fieldName);
        bld.append(":(");
        
        for(int i = 0; i < values.size(); i++)
        {
            if(i != 0) bld.append(" ");
            bld.append(values.get(i));
        }
        
        bld.append(")");
    }
    
    
    private void addSpace()
    {
        if(bld.length() > 0) bld.append(" ");        
    }
    
    
    private void addTerm(String field, String value)
    {
        bld.append(field);
        bld.append(":");
        bld.append(value);
    }
    
    /**
     * Returns a Solr query in Lucene query format.
     */
    public String toString()
    {
        return (bld.length() > 0) ? bld.toString() : null;
    }
}
