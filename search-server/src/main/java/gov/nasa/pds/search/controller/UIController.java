package gov.nasa.pds.search.controller;

import java.util.ArrayList;
import java.util.List;

import org.apache.solr.client.solrj.SolrClient;
import org.apache.solr.client.solrj.SolrQuery;
import org.apache.solr.client.solrj.response.QueryResponse;
import org.apache.solr.common.SolrDocument;
import org.apache.solr.common.SolrDocumentList;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import gov.nasa.pds.search.cfg.SearchServerConfiguration;
import gov.nasa.pds.search.cfg.SolrConfiguration;
import gov.nasa.pds.search.solr.SolrManager;


@RestController
@RequestMapping(path = "/ui")
public class UIController
{
    private static class Item
    {
        public String lid;
        public String title;
        public String product_class;
        public String data_class;
    }
    
    
    @Autowired
    private SearchServerConfiguration ssConfig;

    
    @GetMapping(path = "/search", produces = "application/json")
    public List<Item> getSearch(@RequestParam(name = "q") String pQuery) throws Exception
    {
        List<Item> list = new ArrayList<>();
        
        SolrConfiguration solrCfg = ssConfig.getSolrConfiguration();

        // Create query
        SolrQuery query = new SolrQuery(pQuery);
        if(solrCfg.requestHandler != null)
        {
            query.setRequestHandler(solrCfg.requestHandler);
        }
        query.add("fl", "identifier,title,product_class,data_class");
        query.add("rows", "10");
        
        // Call Solr
        SolrClient solrClient = SolrManager.getInstance().getSolrClient();
        QueryResponse resp = solrClient.query(query);
        SolrDocumentList res = resp.getResults();
        
        // Process search results
        for(SolrDocument doc: res)
        {
            Item item = new Item();

            item.lid = String.valueOf(doc.getFirstValue("identifier"));
            item.title = String.valueOf(doc.getFirstValue("title"));
            item.product_class = String.valueOf(doc.getFirstValue("product_class"));
            item.data_class = String.valueOf(doc.getFirstValue("data_class"));
            
            list.add(item);            
        }
        
        return list;
    }

    
}
