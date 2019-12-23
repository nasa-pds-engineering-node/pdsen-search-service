package gov.nasa.pds.nlp.ner.dic;

import java.io.File;

import gov.nasa.pds.nlp.ner.NerToken;


public interface NerDictionary
{
    public NerToken get(String str);
    public void load(File file);
}
