package gov.nasa.pds.nlp.query;

import java.util.List;

import gov.nasa.pds.nlp.ner.NamedEntityRecognizer;
import gov.nasa.pds.nlp.ner.NerToken;
import gov.nasa.pds.nlp.ner.NerTokenType;
import gov.nasa.pds.nlp.ner.dic.NerDictionary;

/**
 * Classifies a list of tokens (tagged words) into several categories (classes) of context queries,
 * such as an instrument query, target query, etc.
 * 
 * @author karpenko
 */
public class ContextQueryClassifier
{
    private NamedEntityRecognizer ctxNer;
    
    
    public ContextQueryClassifier(NerDictionary dic)
    {
        this.ctxNer = new NamedEntityRecognizer(dic);
    }

    
    public byte classify(List<String> lexTokens, List<NerToken> nerTokens)
    {
        boolean hasInvestigation = false;
        boolean hasInstrument = false;
        boolean hasTarget = false;
        
        // Process generic tokens
        for(NerToken token: nerTokens)
        {
            switch(token.getType())
            {
            case NerTokenType.INVESTIGATION:
            case NerTokenType.INSTRUMENT_HOST:
                hasInvestigation = true;
                break;
            case NerTokenType.INSTRUMENT:
                hasInstrument = true;
                break;
            case NerTokenType.TARGET:
            case NerTokenType.TARGET_TYPE:
                hasTarget = true;
                break;
            }
        }

        // Process context product tokens
        List<NerToken> ctxTokens = ctxNer.parse(lexTokens);
        for(NerToken token: ctxTokens)
        {
            switch(token.getType())
            {
            case NerTokenType.INVESTIGATION_TYPE:
            case NerTokenType.INSTRUMENT_HOST_TYPE:
                hasInvestigation = true;
                break;
            case NerTokenType.INSTRUMENT_TYPE:
                hasInstrument = true;
                break;
            }
        }
        
        if(hasInstrument) return ContextQueryClass.INSTRUMENT;
        if(hasInvestigation) return ContextQueryClass.INVESTIGATION;
        if(hasTarget) return ContextQueryClass.TARGET;
        
        return ContextQueryClass.UNKNOWN;
    }
}
