package tt;

import java.io.PrintWriter;
import java.util.List;

import org.apache.solr.client.solrj.SolrClient;
import org.apache.solr.client.solrj.SolrQuery;
import org.apache.solr.client.solrj.response.FacetField;
import org.apache.solr.client.solrj.response.FacetField.Count;
import org.apache.solr.client.solrj.response.QueryResponse;
import org.apache.solr.common.SolrDocument;
import org.apache.solr.common.SolrDocumentList;

import gov.nasa.pds.search.cfg.ConfigurationLoader;
import gov.nasa.pds.search.cfg.SolrConfiguration;
import gov.nasa.pds.search.solr.QueryResponseJsonWriter;
import gov.nasa.pds.search.solr.util.SolrManager;


public class TestSolrManager
{

    public static void main(String[] args) throws Exception
    {
        System.setProperty("pds.search.server.conf", "/ws/etc/");
        
        SolrConfiguration solrCfg = ConfigurationLoader.load().getSolrConfiguration();
        SolrManager.init(solrCfg);

        
        String facetField = "science_facets";
        
        String strQuery = "investigation_id:maven";
        
        SolrQuery query = new SolrQuery(strQuery);
        query.add("rows", "5");
        query.add("fl", "title");
        
        query.add("facet", "on");
        query.add("facet.field", facetField);
        
        SolrClient solrClient = SolrManager.getInstance().getSolrClient();
        QueryResponse resp = solrClient.query("data", query);

        PrintWriter writer = new PrintWriter(System.out);
        QueryResponseJsonWriter jsonWriter = new QueryResponseJsonWriter(writer);
        jsonWriter.setPrettyPrint();
        jsonWriter.write(resp);
        writer.close();
        
        
        //List<FacetField> list = resp.getFacetFields();
        //System.out.println("*********** " + list.size());
        
        /*
        for(Count cnt: ff.getValues())
        {
            System.out.println(cnt.getName() + " --> " + cnt.getCount());
        }
        */
        
        
        SolrManager.destroy();
    }

}
