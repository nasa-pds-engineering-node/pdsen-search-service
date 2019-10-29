package gov.nasa.pds.search;

import java.io.File;

import javax.annotation.PreDestroy;

import org.springframework.beans.factory.config.ConfigurableListableBeanFactory;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.event.ApplicationContextInitializedEvent;
import org.springframework.context.ApplicationListener;

import gov.nasa.pds.nlp.MultiWordDictionary;
import gov.nasa.pds.nlp.NamedEntityRecognizer;
import gov.nasa.pds.search.cfg.ConfigurationLoader;
import gov.nasa.pds.search.cfg.SearchServerConfiguration;
import gov.nasa.pds.search.solr.SolrManager;

/**
 * Spring Boot application.
 * @author karpenko
 */
@SpringBootApplication
public class SBMain
{
    private static class InitListener implements ApplicationListener<ApplicationContextInitializedEvent>
    {
        private SearchServerConfiguration ssCfg;
        private NamedEntityRecognizer ner;
        
        public InitListener(SearchServerConfiguration ssCfg, NamedEntityRecognizer ner)
        {
            this.ssCfg = ssCfg;
            this.ner = ner;
        }
        
        @Override
        public void onApplicationEvent(ApplicationContextInitializedEvent event)
        {
            // Register beans in Spring application context
            ConfigurableListableBeanFactory beanFact = event.getApplicationContext().getBeanFactory();             

            // Server configuration
            beanFact.registerSingleton(ssCfg.getClass().getCanonicalName(), ssCfg);
            // NER
            beanFact.registerSingleton(ner.getClass().getCanonicalName(), ner);
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
        
        // Init NER        
        MultiWordDictionary dic = new MultiWordDictionary();
        File file = new File(ssCfg.getConfigDirectory(), "ner.dic");
        dic.load(file);
        NamedEntityRecognizer ner = new NamedEntityRecognizer(dic);
        
        // Init Solr
        SolrManager.init(ssCfg.getSolrConfiguration());
        
        // Start Spring application
        SpringApplication app = new SpringApplication(SBMain.class);
        app.addListeners(new InitListener(ssCfg, ner));
        app.run(args);
    }
}
