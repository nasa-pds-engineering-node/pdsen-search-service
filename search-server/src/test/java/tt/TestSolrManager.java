package tt;

import org.apache.solr.client.solrj.SolrClient;
import org.apache.solr.client.solrj.SolrQuery;
import org.apache.solr.client.solrj.response.FacetField;
import org.apache.solr.client.solrj.response.FacetField.Count;
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

        
        String facetField = "science_facets";
        
        //String strQuery = "form-investigation:\"VEGA 1\"";
        //String strQuery = "investigation_name:\"VEGA 1\"";        
        //String strQuery = "+target_name:\"(2003 J4)\"";
        
        String strQuery = "investigation_id:maven";
        
        //String strQuery = "+(target:Mars target:Jupiter target:Pasadena) +investigation:\"Mars Reconnaissance Orbiter\" +product_class:Product_Data_Set_PDS3 +title:derived";
        
        SolrQuery query = new SolrQuery(strQuery);
        query.add("rows", "5");
        query.add("fl", "title");
        
        query.add("facet", "on");
        query.add("facet.field", facetField);
        
        SolrClient solrClient = SolrManager.getInstance().getSolrClient();
        QueryResponse resp = solrClient.query("data", query);

        FacetField ff = resp.getFacetField(facetField);
        for(Count cnt: ff.getValues())
        {
            System.out.println(cnt.getName() + " --> " + cnt.getCount());
        }
        
        /*
        SolrDocumentList docList = resp.getResults();
        System.out.println("Documents: " + docList.getNumFound());
        
        for(SolrDocument doc: docList)
        {
            System.out.println(doc.getFirstValue("title"));
            System.out.println("------------------------------------------------");
        }
        */
        
        SolrManager.destroy();
    }

}
