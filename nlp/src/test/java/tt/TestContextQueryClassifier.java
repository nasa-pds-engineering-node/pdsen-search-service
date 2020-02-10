package tt;

import java.io.File;
import java.util.List;

import gov.nasa.pds.nlp.lex.PdsLexer;
import gov.nasa.pds.nlp.ner.NamedEntityRecognizer;
import gov.nasa.pds.nlp.ner.NerToken;
import gov.nasa.pds.nlp.ner.dic.NerDictionary;
import gov.nasa.pds.nlp.ner.dic.NerDictionaryHashMap;
import gov.nasa.pds.nlp.query.ContextQueryClass;
import gov.nasa.pds.nlp.query.ContextQueryClassifier;


public class TestContextQueryClassifier
{
    NamedEntityRecognizer ner;
    PdsLexer lexer;
    ContextQueryClassifier classifier;

    
    TestContextQueryClassifier()
    {
        ner = createNer();
        lexer = new PdsLexer();
        classifier = createClassifier();
    }
    
    
    private static NamedEntityRecognizer createNer()
    {
        NerDictionary dic = new NerDictionaryHashMap();        
        dic.load(new File("src/main/data/ner/"));
        NamedEntityRecognizer ner = new NamedEntityRecognizer(dic);
        return ner;
    }


    private static ContextQueryClassifier createClassifier()
    {
        NerDictionary dic = new NerDictionaryHashMap();        
        dic.load(new File("src/main/data/classifier/context"));
        ContextQueryClassifier cc = new ContextQueryClassifier(dic);
        return cc;
    }

    
    private void classify(String text) throws Exception
    {
        List<String> lexTokens = lexer.parse(text);
        List<NerToken> nerTokens = ner.parse(lexTokens);
        byte cc = classifier.classify(lexTokens, nerTokens);

        System.out.println(text + "  -->  " + ContextQueryClass.toString(cc));        
    }
    
    
    public static void main(String[] args) throws Exception
    {
        TestContextQueryClassifier test = new TestContextQueryClassifier();
        
        test.classify("rosetta instruments");
        test.classify("rosetta lander instruments");
        test.classify("missions to mars");
        test.classify("lroc");
        test.classify("lro spectrometers");
        test.classify("test");
    }

}
