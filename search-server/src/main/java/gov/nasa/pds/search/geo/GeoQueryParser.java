package gov.nasa.pds.search.geo;

import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import gov.nasa.pds.nlp.NamedEntityRecognizer;
import gov.nasa.pds.nlp.Token;
import gov.nasa.pds.search.feature.FeatureInfo;
import gov.nasa.pds.search.feature.FeatureRepo;

public class GeoQueryParser
{
    private static final Logger LOG = LoggerFactory.getLogger(GeoQueryParser.class);
    
    private NamedEntityRecognizer ner;
    
    public GeoQueryParser(NamedEntityRecognizer ner)
    {
        this.ner = ner;
    }
    
    public BaseGeoQuery parse(String text)
    {
        List<Token> tokens = ner.parse(text);
        
        MetadataQuery mq = new MetadataQuery();
        
        for(Token token: tokens)
        {
            switch(token.type)
            {
            case Token.TYPE_TARGET:
                mq.targetId = token.text;
                break;
            case Token.TYPE_INVESTIGATION:
            case Token.TYPE_INVESTIGATION_AND_HOST:
                mq.missionId = token.text;
                break;
            case Token.TYPE_INSTRUMENT:
                mq.instrumentId = token.text;
                break;
            case Token.TYPE_PRODUCT_TYPE:
                mq.productType = token.text;
                break;
            case Token.TYPE_FEATURE:                
                FeatureInfo fi = FeatureRepo.getInstance().findById(token.text);
                if(fi == null)
                {
                    LOG.warn("Could not find feature with ID = " + token.text);
                }
                else
                {
                    mq.featureName = fi.name;
                    mq.featureType = fi.type;
                }
                break;
            }
        }
        
        return mq;
    }
}
