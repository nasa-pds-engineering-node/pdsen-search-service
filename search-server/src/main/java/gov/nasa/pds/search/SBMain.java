package gov.nasa.pds.search;

import java.io.File;

import javax.annotation.PreDestroy;

import org.springframework.beans.factory.config.ConfigurableListableBeanFactory;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.event.ApplicationContextInitializedEvent;
import org.springframework.context.ApplicationListener;

import gov.nasa.pds.nlp.ner.NamedEntityRecognizer;
import gov.nasa.pds.nlp.ner.dic.NerDictionary;
import gov.nasa.pds.nlp.ner.dic.NerDictionaryHashMap;
import gov.nasa.pds.nlp.query.ContextQueryClassifier;
import gov.nasa.pds.search.cfg.ConfigurationLoader;
import gov.nasa.pds.search.cfg.SearchServerConfiguration;
import gov.nasa.pds.search.solr.util.SolrManager;


/**
 * Spring Boot application.
 * @author karpenko
 */
@SpringBootApplication
public class SBMain
{
    private static class AppListener implements ApplicationListener<ApplicationContextInitializedEvent>
    {
        private SearchServerConfiguration ssCfg;
        private NamedEntityRecognizer ner;
        private ContextQueryClassifier ctxClassifier;
        
        
        public AppListener(SearchServerConfiguration ssCfg, NamedEntityRecognizer ner,
                ContextQueryClassifier ctxClassifier)
        {
            this.ssCfg = ssCfg;
            this.ner = ner;
            this.ctxClassifier = ctxClassifier;
        }
        
        
        @Override
        public void onApplicationEvent(ApplicationContextInitializedEvent event)
        {
            // Register beans in Spring application context
            ConfigurableListableBeanFactory beanFact = event.getApplicationContext().getBeanFactory();             

            beanFact.registerSingleton(ssCfg.getClass().getCanonicalName(), ssCfg);
            beanFact.registerSingleton(ner.getClass().getCanonicalName(), ner);
            beanFact.registerSingleton(ctxClassifier.getClass().getCanonicalName(), ctxClassifier);
        }
    }
    
    
    @PreDestroy
    public void onExit() 
    {
        SolrManager.destroy();
    }
    
    
    public static void main(String[] args) throws Exception
    {
        // Load configuration
        SearchServerConfiguration ssCfg = ConfigurationLoader.load();
        
        // Init generic NER
        NerDictionary nerDic = new NerDictionaryHashMap();
        nerDic.load(new File(ssCfg.getConfigDirectory(), "ner"));
        NamedEntityRecognizer ner = new NamedEntityRecognizer(nerDic);
        
        // Init context product classifier
        NerDictionary ctxDic = new NerDictionaryHashMap();
        ctxDic.load(new File(ssCfg.getConfigDirectory(), "classifier/context"));
        ContextQueryClassifier ctxClassifier = new ContextQueryClassifier(ctxDic);
        
        // Init Solr
        SolrManager.init(ssCfg.getSolrConfiguration());
        
        // Start Spring application
        AppListener appListener = new AppListener(ssCfg, ner, ctxClassifier);
        SpringApplication app = new SpringApplication(SBMain.class);
        app.addListeners(appListener);
        app.run(args);
    }
}
