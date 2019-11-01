package gov.nasa.pds.search.geo;

import java.util.List;

import gov.nasa.pds.nlp.NamedEntityRecognizer;
import gov.nasa.pds.nlp.Token;

public class GeoQueryParser
{
    private NamedEntityRecognizer ner;
    
    public GeoQueryParser(NamedEntityRecognizer ner)
    {
        this.ner = ner;
    }
    
    public BaseGeoQuery parse(String text)
    {
        List<Token> tokens = ner.parse(text);
        for(Token token: tokens)
        {
            System.out.println(token.text + " --> " + token.type);
        }
        
        MetadataQuery mq = new MetadataQuery();
        return mq;
    }
}
