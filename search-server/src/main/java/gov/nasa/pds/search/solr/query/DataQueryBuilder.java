package gov.nasa.pds.search.solr.query;

import java.util.ArrayList;
import java.util.List;

import org.apache.solr.client.solrj.SolrQuery;

import gov.nasa.pds.nlp.ner.NerToken;
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
        
        List<String> unknownTokens = new ArrayList<String>();
        
        for(NerToken token: nerTokens)
        {
            switch(token.getType())
            {
            case NerToken.TYPE_TARGET:
                targetId = getProductId(token);
                break;
            case NerToken.TYPE_INSTRUMENT:
                instrumentId = getProductId(token);
                break;
            case NerToken.TYPE_INSTRUMENT_HOST:
                instrumentHostId = getProductId(token);
                break;
            case NerToken.TYPE_INVESTIGATION:
                investigationId = getProductId(token);
                break;
            default:
                unknownTokens.add(token.getKey());    
            }
        }

        LuceneQueryBuilder bld = new LuceneQueryBuilder();
        bld.addRequiredField("target", targetId);
        bld.addRequiredField("investigation_id", investigationId);
        bld.addRequiredField("instrument_id", instrumentId);
        bld.addRequiredField("instrument_host_id", instrumentHostId);

        bld.addRequiredField("title", unknownTokens);
        
        String queryString = bld.toString();
        if(queryString.isEmpty()) return null;
        
        SolrQuery query = new SolrQuery(queryString);
        return query;
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
