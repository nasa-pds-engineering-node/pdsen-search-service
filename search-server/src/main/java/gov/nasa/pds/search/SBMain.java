package gov.nasa.pds.search;

import javax.annotation.PreDestroy;

import org.springframework.beans.factory.config.ConfigurableListableBeanFactory;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.event.ApplicationContextInitializedEvent;
import org.springframework.context.ApplicationListener;

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
        
        public InitListener(SearchServerConfiguration ssCfg)
        {
            this.ssCfg = ssCfg;
        }
        
        @Override
        public void onApplicationEvent(ApplicationContextInitializedEvent event)
        {
            // Register beans in Spring application context
            ConfigurableListableBeanFactory beanFact = event.getApplicationContext().getBeanFactory();             
            // Server configuration
            beanFact.registerSingleton(ssCfg.getClass().getCanonicalName(), ssCfg);
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
        
        // Init Solr manager
        SolrManager.init(ssCfg.getSolrConfiguration());
        
        // Start Spring application
        SpringApplication app = new SpringApplication(SBMain.class);
        app.addListeners(new InitListener(ssCfg));
        app.run(args);
    }
}
