package gov.nasa.pds.data.solr;

import java.io.FileWriter;
import java.io.Writer;
import java.text.SimpleDateFormat;
import java.util.Collection;

import org.apache.commons.lang.StringEscapeUtils;
import org.apache.solr.client.solrj.SolrClient;
import org.apache.solr.client.solrj.SolrQuery;
import org.apache.solr.client.solrj.SolrQuery.ORDER;
import org.apache.solr.client.solrj.impl.HttpSolrClient;
import org.apache.solr.client.solrj.response.QueryResponse;
import org.apache.solr.common.SolrDocument;

/**
 * Export data from old versions of Solr that don't support cursors (cursorMark).
 * @author karpenko
 */
public class LegacyExporter
{
    private static final SimpleDateFormat DATE_FORMAT = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss");
    
    // Batch size = PAGE_SIZE * NUM_PAGES = 1000 rows. 
    private static final int PAGE_SIZE = 50;
    private static final int NUM_PAGES = 20;
    
    private String requestHandler;
    
    private SolrClient solrClient;
    private SolrQuery query;
    
    
    public LegacyExporter(String solrUrl, String requestHandler)
    {
        solrClient = new HttpSolrClient.Builder(solrUrl).build();
    }

    
    public void setQuery(String strQuery, String primaryKey)
    {
        query = new SolrQuery(strQuery);

        if(requestHandler != null)
        {
            query.setRequestHandler(requestHandler);
        }

        if(primaryKey != null)
        {
            query.addSort(primaryKey, ORDER.asc);
        }
    }
  

    public void close()
    {
        try
        {
            solrClient.close();
        }
        catch(Exception ex)
        {
            // Ignore
        }
    }
    
    
    public void export(String exportPath) throws Exception
    {
        // Find total number of rows
        query.setRows(0);
        QueryResponse resp = solrClient.query(query);
        long numDocs = resp.getResults().getNumFound();
        
        // Find number of batches
        long batchSize = PAGE_SIZE * NUM_PAGES;        
        long numBatches = numDocs / batchSize;
        if(numDocs % batchSize != 0) numBatches++;
        
        System.out.println("Number of records = " + numDocs);
        System.out.println("Batch size = " + batchSize);
        System.out.println("Number of batches = " + numBatches);

        // Base path for all batches
        String basePath = exportPath;
        if(exportPath.endsWith(".xml"))
        {
            basePath = exportPath.substring(0, exportPath.length() - 4);
        }

        // Run batches
        for(int i = 0; i < numBatches; i++)
        {
            String batchPath = basePath + "-" + i + ".xml";
            exportBatch(batchPath, i);
        }        
    }
    
    
    public void exportBatch(String exportPath, int batchNum) throws Exception
    {
        System.out.println("Batch " + batchNum);
        
        Writer writer = new FileWriter(exportPath);
        writer.write("<root>\n");
        
        int page = NUM_PAGES * batchNum;
        for(int i = 0; i < NUM_PAGES; i++)
        {
            query.setStart(page * PAGE_SIZE);
            query.setRows(PAGE_SIZE);
            
            QueryResponse resp = solrClient.query(query);            

            for(SolrDocument doc: resp.getResults())
            {
                exportDoc(doc, writer);
            }

            writer.flush();
            System.out.println("Done page " + page);
            page++;
            
            if(resp.getResults().size() < PAGE_SIZE) break;
        }

        writer.write("</root>\n");
        writer.close();        
    }

    
    private static void exportDoc(SolrDocument doc, Writer writer) throws Exception
    {
        writer.write("<doc>\n");
        
        for(String fieldName: doc.getFieldNames())
        {
            Collection<Object> values = doc.getFieldValues(fieldName);
            if(values != null && values.size() > 0)
            {
                for(Object value: values)
                {
                    writeField(writer, fieldName, value);
                }
            }
        }
        
        writer.write("</doc>\n");
    }

    
    private static void writeField(Writer writer, String key, Object value) throws Exception
    {
        writer.write("  <field name=\"");
        writer.write(key);
        writer.write("\">");
        
        String strValue;
        if(value instanceof java.util.Date)
        {
            strValue = DATE_FORMAT.format(value);
        }
        else
        {
            strValue = value.toString();
        }
        
        writer.write(StringEscapeUtils.escapeXml(strValue));
        
        writer.write("</field>\n");
    }
    
}
