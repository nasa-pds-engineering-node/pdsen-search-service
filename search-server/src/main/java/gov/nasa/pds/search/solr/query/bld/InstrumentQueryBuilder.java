package gov.nasa.pds.search.solr.query.bld;

import java.util.ArrayList;
import java.util.List;

import org.apache.solr.client.solrj.SolrQuery;

import gov.nasa.pds.nlp.ner.NerToken;
import gov.nasa.pds.nlp.ner.NerTokenType;
import gov.nasa.pds.search.solr.util.LuceneQueryBuilder;

public class InstrumentQueryBuilder
{
    private List<NerToken> nerTokens;
    
    
    public InstrumentQueryBuilder(List<NerToken> nerTokens)
    {
        this.nerTokens = nerTokens;
    }


    public SolrQuery build()
    {
        String investigationId = null;
        String instrumentId = null;
        String instrumentHostId = null;
        
        List<String> unknownTokens = new ArrayList<String>();
        
        for(NerToken token: nerTokens)
        {
            switch(token.getType())
            {
            case NerTokenType.INSTRUMENT:
                instrumentId = getProductId(token);
                break;
            case NerTokenType.INSTRUMENT_HOST:
                instrumentHostId = getProductId(token);
                break;
            case NerTokenType.INVESTIGATION:
                investigationId = getProductId(token);
                break;
            default:
                addUnknownToken(unknownTokens, token.getKey());
            }
        }

        LuceneQueryBuilder bld = new LuceneQueryBuilder();
        bld.addField(true, "investigation_id", investigationId);
        bld.addField(true, "instrument_id", instrumentId);
        bld.addField(true, "instrument_host_id", instrumentHostId);

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
        if(token.equals("instrument") 
                || token.equals("instruments")) return;
        
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
