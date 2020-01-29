package gov.nasa.pds.nlp.query;

import java.util.List;

import gov.nasa.pds.nlp.ner.NerToken;
import gov.nasa.pds.nlp.ner.NerTokenType;

/**
 * Classifies a list of tokens (tagged words) into several categories (classes) of context queries,
 * such as an instrument query, target query, etc.
 * 
 * @author karpenko
 */
public class ContextQueryClassifier
{
    public byte classify(List<NerToken> tokens)
    {
        boolean hasInvestigation = false;
        boolean hasInstrument = false;
        boolean hasTarget = false;
        
        for(NerToken token: tokens)
        {
            switch(token.getType())
            {
            case NerTokenType.INVESTIGATION:
            case NerTokenType.INVESTIGATION_TYPE:
            case NerTokenType.INSTRUMENT_HOST:
            case NerTokenType.INSTRUMENT_HOST_TYPE:
                hasInvestigation = true;
                break;
            case NerTokenType.INSTRUMENT:
            case NerTokenType.INSTRUMENT_TYPE:
                hasInstrument = true;
                break;
            case NerTokenType.TARGET:
            case NerTokenType.TARGET_TYPE:
                hasTarget = true;
                break;
            }
        }

        if(hasInstrument) return ContextQueryClass.INSTRUMENT;
        if(hasInvestigation) return ContextQueryClass.INVESTIGATION;
        if(hasTarget) return ContextQueryClass.TARGET;
        
        return ContextQueryClass.UNKNOWN;
    }
}
