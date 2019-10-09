package gov.nasa.pds.search.solr;

public class SolrQueryStringBuilder
{
    private StringBuilder bld; 
    
    
    public SolrQueryStringBuilder()
    {
        bld = new StringBuilder();
    }
    
    
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
    
    
    public String toString()
    {
        return (bld.length() > 0) ? bld.toString() : null;
    }
}
