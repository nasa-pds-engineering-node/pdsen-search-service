package gov.nasa.pds.search.solr;

import java.io.IOException;
import java.io.OutputStream;
import java.util.Collection;

import org.apache.solr.common.SolrDocument;
import org.apache.solr.common.SolrDocumentList;

import com.fasterxml.jackson.core.JsonEncoding;
import com.fasterxml.jackson.core.JsonFactory;
import com.fasterxml.jackson.core.JsonGenerator;


public class JsonResponseWriter
{
    private JsonFactory jFactory;
    
    public JsonResponseWriter()
    {
        jFactory = new JsonFactory();
    }

    
    public void error(OutputStream out, String msg) throws IOException
    {
        JsonGenerator jgen = jFactory.createGenerator(out, JsonEncoding.UTF8);
        
        jgen.writeStartObject(); // Root
        
        jgen.writeFieldName("response");
        jgen.writeStartObject();
        {
            jgen.writeStringField("status", "error");
            jgen.writeStringField("errorText", msg);
        }
        jgen.writeEndObject();
        
        jgen.writeEndObject(); // Root
        
        jgen.close();
    }

    
    public void write(OutputStream out, SolrDocumentList docList) throws IOException
    {
        JsonGenerator jgen = jFactory.createGenerator(out, JsonEncoding.UTF8);
        
        jgen.writeStartObject(); // Root

        jgen.writeFieldName("response");
        jgen.writeStartObject(); // Response
        {
            jgen.writeStringField("status", "ok");
            jgen.writeNumberField("numFound", docList.getNumFound());
            jgen.writeNumberField("start", docList.getStart());
            jgen.writeNumberField("rows", docList.size());
            // Docs
            jgen.writeFieldName("docs"); writeDocs(jgen, docList);
        }
        jgen.writeEndObject(); // Response
        
        jgen.writeEndObject(); // Root
        
        jgen.close();
    }
    
    
    private void writeDocs(JsonGenerator jgen, SolrDocumentList docList) throws IOException
    {
        jgen.writeStartArray();
        
        for(SolrDocument doc: docList)
        {
            jgen.writeStartObject();
            
            String fieldName = "title";
            Object value = doc.getFieldValue(fieldName); 
            writeField(jgen, fieldName, value);
            
            jgen.writeEndObject();
        }
        
        jgen.writeEndArray();
    }
    
    
    @SuppressWarnings("unchecked")
    private void writeField(JsonGenerator jgen, String name, Object value) throws IOException
    {
        if(value == null) return;
        
        jgen.writeFieldName(name);
        
        if(value instanceof Collection) 
        {
            Collection<Object> values = (Collection<Object>)value;
            
            jgen.writeStartArray();
            
            for(Object obj: values)
            {
                jgen.writeObject(obj);
            }
            
            jgen.writeEndArray();
        }
        else
        {
            jgen.writeObject(value);
        }
    }
    
}
