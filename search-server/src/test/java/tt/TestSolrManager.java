package tt;

import org.apache.solr.client.solrj.SolrClient;
import org.apache.solr.client.solrj.SolrQuery;
import org.apache.solr.client.solrj.response.QueryResponse;
import org.apache.solr.common.SolrDocument;
import org.apache.solr.common.SolrDocumentList;

import gov.nasa.pds.search.cfg.ConfigurationLoader;
import gov.nasa.pds.search.cfg.SolrConfiguration;
import gov.nasa.pds.search.solr.util.SolrManager;


public class TestSolrManager
{

    public static void main(String[] args) throws Exception
    {
        System.setProperty("pds.search.server.conf", "/ws/etc/");
        
        SolrConfiguration solrCfg = ConfigurationLoader.load().getSolrConfiguration();
        SolrManager.init(solrCfg);

        
        //String strQuery = "form-investigation:\"VEGA 1\"";
        //String strQuery = "investigation_name:\"VEGA 1\"";        
        //String strQuery = "+target_name:\"(2003 J4)\"";
        
        String strQuery = "+target_name:\"9018+029\"";
        
        //String strQuery = "+(target:Mars target:Jupiter target:Pasadena) +investigation:\"Mars Reconnaissance Orbiter\" +product_class:Product_Data_Set_PDS3 +title:derived";
        
        SolrQuery query = new SolrQuery(strQuery);
        query.add("rows", "10");
        query.add("fl", "identifier,title,product_class,data_class");
        
        
        SolrClient solrClient = SolrManager.getInstance().getSolrClient();
        QueryResponse resp = solrClient.query("pds", query);
        SolrDocumentList res = resp.getResults();
        System.out.println("Found: " + res.getNumFound());
        
        for(SolrDocument doc: res)
        {
            System.out.println(doc.getFirstValue("identifier"));
            System.out.println(doc.getFirstValue("title"));
            System.out.println(doc.getFirstValue("product_class"));
            System.out.println(doc.getFirstValue("data_class"));            
            System.out.println("------------------------------------------------");
        }
        
        SolrManager.destroy();
    }

}
