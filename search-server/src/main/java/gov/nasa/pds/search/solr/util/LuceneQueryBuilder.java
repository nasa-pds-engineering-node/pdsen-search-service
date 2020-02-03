package gov.nasa.pds.search.solr.util;

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

        
    public void addGroupStart(boolean required)
    {
        addSpace();
        if(required) bld.append("+");
        bld.append("(");
    }
    

    public void addGroupEnd()
    {
        bld.append(")");
    }


    public void addBoost(int boost)
    {
        bld.append("^" + boost);
    }

    /**
     * Add query field.
     * @param field
     * @param values
     */
    public void addField(boolean required, String field, String value)
    {
        if(value == null) return;
        
        addSpace();
        if(required) bld.append("+");

        bld.append(field);
        bld.append(":\"");
        bld.append(value);
        bld.append("\"");
    }
    
    
    public void addField(boolean required, String fieldName, List<String> values)
    {
        if(values == null || values.size() == 0) return;
        
        addSpace();
        
        if(required) bld.append("+");
        
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
        if(bld.length() == 0) return; 
        
        char lastChar = bld.charAt(bld.length()-1); 
        
        if(lastChar != '(') bld.append(" ");
    }
    
    
    /**
     * Returns query string.
     */
    public String toString()
    {
        return (bld.length() > 0) ? bld.toString() : null;
    }
}
