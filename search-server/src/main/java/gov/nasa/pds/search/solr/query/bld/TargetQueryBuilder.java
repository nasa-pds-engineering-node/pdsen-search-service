package gov.nasa.pds.search.solr.query.bld;

import java.util.ArrayList;
import java.util.List;

import org.apache.solr.client.solrj.SolrQuery;

import gov.nasa.pds.nlp.ner.NerToken;
import gov.nasa.pds.nlp.ner.NerTokenType;
import gov.nasa.pds.solr.query.LuceneQueryBuilder;


public class TargetQueryBuilder
{
    private List<NerToken> nerTokens;
    
    
    public TargetQueryBuilder(List<NerToken> nerTokens)
    {
        this.nerTokens = nerTokens;
    }


    public SolrQuery build()
    {
        String targetId = null;
        String targetType = null;
        
        List<String> unknownTokens = new ArrayList<String>();
        
        for(NerToken token: nerTokens)
        {
            switch(token.getType())
            {
            case NerTokenType.TARGET:
                targetId = getProductId(token);
                break;
            case NerTokenType.TARGET_TYPE:
                targetType = getProductId(token);
                break;
            default:
                addUnknownToken(unknownTokens, token.getKey());
            }
        }

        LuceneQueryBuilder bld = new LuceneQueryBuilder();

        // "Mars moons" || "Satellites of Jupiter"
        if(targetId != null && "satellite".equals(targetType))
        {
            bld.addField(true, "id_of_primary", targetId);
            bld.addField(true, "target_type", targetType);
        }
        else
        {
            bld.addField(true, "target_id", targetId);
            bld.addField(true, "target_type", targetType);
        }
        
        // Unknown tokens
        if(!unknownTokens.isEmpty())
        {
            bld.addField(true, "search_p1", unknownTokens);
        }
        
        String queryString = bld.toString();
        if(queryString == null || queryString.isEmpty()) return null;
        
        SolrQuery query = new SolrQuery(queryString);
        return query;
    }
    
    
    private static void addUnknownToken(List<String> unknownTokens, String token)
    {
        //TODO: Properly handle data query stop words
        if(token.equals("of")) return;
        
        unknownTokens.add(token);
    }
    
    
    private String getProductId(NerToken token)
    {
        String id = token.getId();
        if(id == null) 
        {
            id = token.getKey().replaceAll("\\s", "_");
        }
        
        return id;
    }
    
}
