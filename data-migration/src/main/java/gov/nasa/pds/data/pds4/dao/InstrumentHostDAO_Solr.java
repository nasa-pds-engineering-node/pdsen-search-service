package gov.nasa.pds.data.pds4.dao;

import org.apache.solr.client.solrj.SolrClient;
import org.apache.solr.client.solrj.SolrQuery;
import org.apache.solr.client.solrj.response.QueryResponse;
import org.apache.solr.common.SolrDocumentList;
import org.apache.solr.common.SolrInputDocument;

import gov.nasa.pds.data.pds4.model.InstrumentHost;


public class InstrumentHostDAO_Solr implements InstrumentHostDAO
{
    private static final String STG_INSTRUMENT_HOST = "stg_instrument_host";

    public InstrumentHostDAO_Solr()
    {
    }
    
    public float getVersion(String lid) throws Exception
    {
        SolrQuery query = new SolrQuery("lid:\"" + lid + "\"");
        
        SolrClient client = SolrManager.getInstance().getSolrClient();
        QueryResponse response = client.query(STG_INSTRUMENT_HOST, query);
        
        SolrDocumentList documents = response.getResults();
        if(documents.getNumFound() == 0)
        {
            return -1;
        }
        else
        {
            Object obj = documents.get(0).getFieldValue("vid");
            return (float)obj;
        }
    }

    
    public void save(InstrumentHost ih) throws Exception
    {
        final SolrInputDocument doc = new SolrInputDocument();

        // Ids
        doc.addField("lid", ih.lid);
        doc.addField("vid", ih.vid);       

        doc.addField("id", ih.id);
        doc.addField("name", ih.name);
        doc.addField("type", ih.type);
        doc.addField("description", ih.description);
        
        // Metadata
        addField(doc, "investigation_ref", ih.investigationRef);
        addField(doc, "instrument_ref", ih.instrumentRef);
        addField(doc, "target_ref", ih.targetRef);

        // Save the document
        SolrClient client = SolrManager.getInstance().getSolrClient();
        client.add(STG_INSTRUMENT_HOST, doc);
        client.commit(STG_INSTRUMENT_HOST);
    }

    
    public void update(InstrumentHost ih) throws Exception
    {
        float oldVersion = getVersion(ih.lid);
        if(ih.vid > oldVersion)
        {
            save(ih);
        }
    }

    
    private static void addField(SolrInputDocument doc, String name, Object value)
    {
        if(value == null) return;
        doc.addField(name, value);
    }

}
