package gov.nasa.pds.search.geo;

import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import gov.nasa.pds.nlp.NamedEntityRecognizer;
import gov.nasa.pds.nlp.Token;
import gov.nasa.pds.search.ctx.InvestigationInfo;
import gov.nasa.pds.search.ctx.InvestigationRepo;
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
        
        // LID query
        int lidIdx = findLidIndex(tokens);
        if(lidIdx >= 0)
        {
            LidQuery lq = new LidQuery(tokens.get(lidIdx).text);
            
            // LID is not the last token
            if(lidIdx < tokens.size()-1)
            {
                Token nextToken = tokens.get(lidIdx + 1);
                if(nextToken.text.equalsIgnoreCase("files"))
                {
                    lq.listFiles = true;
                }
            }
            
            return lq;
        }
        
        // Metadata query
        MetadataQuery mq = createMetadataQuery(tokens);
        return mq;
    }

    
    private MetadataQuery createMetadataQuery(List<Token> tokens)
    {
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
                // Lookup feature name and type by ID
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
        
        fixTarget(mq);
        return mq;
    }
    

    private void fixTarget(MetadataQuery mq)
    {
        if(mq.targetId == null)
        {
            // Try getting target from mission
            if(mq.missionId != null)
            {
                InvestigationInfo fi = InvestigationRepo.getInstance().findById(mq.missionId);
                if(fi == null)
                {
                    LOG.warn("Could not find investigation with ID = " + mq.missionId);
                }
                else
                {
                    mq.targetId = fi.target;
                }
            }
        }
    }
    
    
    private int findLidIndex(List<Token> tokens)
    {
        for(int i = 0; i < tokens.size(); i++)
        {
            Token token = tokens.get(i);
            if(token.type == Token.TYPE_LID) return i;
        }
        
        return -1;
    }
}
