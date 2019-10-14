package gov.nasa.pds.search.solr;

/**
 * Builds a Solr query string in Lucene query format.
 * @author karpenko
 */
public class SolrQueryStringBuilder
{
    private StringBuilder bld; 
    
    /**
     * Constructor.
     */
    public SolrQueryStringBuilder()
    {
        bld = new StringBuilder();
    }
    
    /**
     * Add query field.
     * @param field
     * @param values
     */
    public void addField(String field, String[] values)
    {
        if(values == null) return;
        
        if(values.length == 1)
        {
            addSpace();
            bld.append("+"); // MUST
            addTerm(field, values[0]);
        }
        else
        {
            addSpace();
            bld.append("+("); // MUST
            
            // SHOULD
            for(int i = 0; i < values.length; i++)
            {
                if(i != 0) bld.append(" ");
                addTerm(field, values[i]);
            }
            
            bld.append(")");
        }
    }
    
    
    private void addSpace()
    {
        if(bld.length() > 0) bld.append(" ");        
    }
    
    
    private void addTerm(String field, String value)
    {
        bld.append(field);
        bld.append(":");
        bld.append("\"");
        bld.append(value);
        bld.append("\"");
    }
    
    /**
     * Returns a Solr query in Lucene query format.
     */
    public String toString()
    {
        return (bld.length() > 0) ? bld.toString() : null;
    }
}
