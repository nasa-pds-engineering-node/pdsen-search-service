package gov.nasa.pds.search.solr.query;

import java.util.ArrayList;
import java.util.List;

import org.apache.solr.client.solrj.SolrQuery;

import gov.nasa.pds.nlp.ner.NerToken;
import gov.nasa.pds.nlp.ner.NerTokenType;
import gov.nasa.pds.search.solr.LuceneQueryBuilder;

public class DataQueryBuilder
{
    private List<NerToken> nerTokens;
    
    
    public DataQueryBuilder(List<NerToken> nerTokens)
    {
        this.nerTokens = nerTokens;
    }


    public SolrQuery build()
    {
        String investigationId = null;
        String instrumentId = null;
        String instrumentHostId = null;
        String targetId = null;
        String targetType = null;
        String collectionType = null;
        
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
            case NerTokenType.INSTRUMENT:
                instrumentId = getProductId(token);
                break;
            case NerTokenType.INSTRUMENT_HOST:
                instrumentHostId = getProductId(token);
                break;
            case NerTokenType.INVESTIGATION:
                investigationId = getProductId(token);
                break;
            case NerTokenType.DATA_TYPE:
                collectionType = getProductId(token);
                break;
            default:
                addUnknownToken(unknownTokens, token.getKey());
            }
        }

        LuceneQueryBuilder bld = new LuceneQueryBuilder();
        bld.addRequiredField("target_name", targetId);
        bld.addRequiredField("target_type", targetType);
        bld.addRequiredField("investigation_id", investigationId);
        bld.addRequiredField("instrument_id", instrumentId);
        bld.addRequiredField("instrument_host_id", instrumentHostId);
        bld.addRequiredField("collection_type", collectionType);

        bld.addRequiredField("search_p1", unknownTokens);
        
        String queryString = bld.toString();
        if(queryString.isEmpty()) return null;
        
        SolrQuery query = new SolrQuery(queryString);
        return query;
    }
    
    
    private static void addUnknownToken(List<String> unknownTokens, String token)
    {
        //TODO: Properly handle data query stop words
        if(token.equals("data")) return;
        
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
    
    
    //TODO: Encode or strip out special Lucene query characters:
    // + - && || ! ( ) { } [ ] ^ " ~ * ? : \
    private String encode(String str)
    {
        return str;
    }
    
}
