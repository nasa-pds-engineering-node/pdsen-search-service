package gov.nasa.pds.search;

import javax.annotation.PreDestroy;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.event.ApplicationContextInitializedEvent;
import org.springframework.context.ApplicationListener;

import gov.nasa.pds.search.cfg.ConfigurationManager;
import gov.nasa.pds.search.solr.SolrManager;


@SpringBootApplication
public class SBMain
{
    private static class InitListener implements ApplicationListener<ApplicationContextInitializedEvent>
    {
        @Override
        public void onApplicationEvent(ApplicationContextInitializedEvent event)
        {
            // Configuration manager 
            ConfigurationManager.init();
            
            // Solr manager
            ConfigurationManager cfgMgr = ConfigurationManager.getInstance();
            SolrManager.init(cfgMgr.getSearchServerConfiguration().getSolrConfiguration());
        }
    }
    
    
    @PreDestroy
    public void onExit() 
    {
        SolrManager.destroy();
    }
    
    
    public static void main(String[] args)
    {
        SpringApplication app = new SpringApplication(SBMain.class);
        app.addListeners(new InitListener());
        app.run(args);
    }
}
