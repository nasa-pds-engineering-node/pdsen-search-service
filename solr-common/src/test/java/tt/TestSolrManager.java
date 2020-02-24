package tt;

import java.io.File;
import org.apache.solr.client.solrj.SolrClient;
import org.apache.solr.client.solrj.SolrQuery;
import org.apache.solr.client.solrj.response.QueryResponse;
import org.apache.solr.common.SolrDocument;
import org.apache.solr.common.SolrDocumentList;

import gov.nasa.pds.solr.SolrManager;
import gov.nasa.pds.solr.cfg.SolrConfiguration;
import gov.nasa.pds.solr.cfg.SolrConfigurationLoader;


public class TestSolrManager
{

    public static void main(String[] args) throws Exception
    {
        SolrConfiguration solrCfg = SolrConfigurationLoader.load(new File("/ws/etc"));
        SolrManager.init(solrCfg);

        String strQuery = "investigation_id:maven";
        
        SolrQuery query = new SolrQuery("*:*");
        query.addFilterQuery(strQuery);
        query.addFilterQuery("title:\"electron density\"");
        
        query.add("rows", "5");
        query.add("fl", "title,id");
        
        SolrClient solrClient = SolrManager.getInstance().getSolrClient();
        QueryResponse resp = solrClient.query("data", query);
        
        SolrDocumentList docs = resp.getResults();
        
        for(SolrDocument doc: docs)
        {
            System.out.println(doc.getFirstValue("title"));
        }
        
        
        SolrManager.destroy();
    }

}
