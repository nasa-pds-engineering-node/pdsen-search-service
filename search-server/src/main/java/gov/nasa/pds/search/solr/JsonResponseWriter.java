package gov.nasa.pds.search.solr;

import java.io.IOException;
import java.io.OutputStream;
import java.io.Writer;
import java.util.Collection;
import java.util.List;

import org.apache.solr.common.SolrDocument;
import org.apache.solr.common.SolrDocumentList;

import com.fasterxml.jackson.core.JsonEncoding;
import com.fasterxml.jackson.core.JsonFactory;
import com.fasterxml.jackson.core.JsonGenerator;

import gov.nasa.pds.search.util.NameMapper;


/**
 * Writes Solr response in JSON format. 
 * @author karpenko
 */
public class JsonResponseWriter implements IResponseWriter
{
    private JsonGenerator jgen;
    private List<String> fields;
    private NameMapper nameMapper;
    
    /**
     * Constructor
     * @param out Output Stream for JSON.
     * @throws IOException
     */
    public JsonResponseWriter(Writer writer) throws IOException
    {
        JsonFactory jFactory = new JsonFactory();
        jgen = jFactory.createGenerator(writer);
    }


    /**
     * Set a list of fields to write.
     * @param fields A list of fields to write.
     */
    public void setFields(List<String> fields)
    {
        this.fields = fields;
    }
    
    
    /**
     * Set name mapper to map public and internal parameters.
     * @param nameMapper
     */
    public void setNameMapper(NameMapper nameMapper)
    {
        this.nameMapper = nameMapper;
    }

    
    /**
     * Write error message
     * @param msg
     * @throws IOException
     */
    public void error(String msg) throws IOException
    {
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

    
    /**
     * Write a list of Solr documents.
     * @param docList
     * @throws IOException
     */
    public void write(SolrDocumentList docList) throws IOException
    {
        jgen.writeStartObject(); // Root

        jgen.writeFieldName("response");
        jgen.writeStartObject(); // Response
        {
            // Header
            jgen.writeStringField("status", "ok");
            jgen.writeNumberField("numFound", docList.getNumFound());
            jgen.writeNumberField("start", docList.getStart());
            jgen.writeNumberField("rows", docList.size());
            // Docs
            jgen.writeFieldName("docs"); writeDocs(docList);
        }
        jgen.writeEndObject(); // Response
        
        jgen.writeEndObject(); // Root
        
        jgen.close();
    }
    
    
    private void writeDocs(SolrDocumentList docList) throws IOException
    {
        jgen.writeStartArray();
        
        for(SolrDocument doc: docList)
        {
            jgen.writeStartObject();
            
            for(String fieldName: fields)
            {
                Object value = doc.getFieldValue(fieldName); 
                writeField(fieldName, value);
            }
            
            jgen.writeEndObject();
        }
        
        jgen.writeEndArray();
    }
    
    
    @SuppressWarnings("unchecked")
    private void writeField(String name, Object value) throws IOException
    {
        if(value == null) return;
        
        String publicName = (nameMapper == null) ? name : nameMapper.findPublicByInternal(name);        
        jgen.writeFieldName(publicName);
        
        if(value instanceof Collection) 
        {
            Collection<Object> values = (Collection<Object>)value;
            writeCollection(values);
        }
        else
        {
            jgen.writeObject(value);
        }
    }
    
    
    private void writeCollection(Collection<Object> values) throws IOException
    {
        if(values == null || values.size() == 0) return;
        
        if(values.size() == 1)
        {
            jgen.writeObject(values.iterator().next());
        }
        else
        {
            jgen.writeStartArray();            
            for(Object obj: values)
            {
                jgen.writeObject(obj);
            }            
            jgen.writeEndArray();
        }
        
    }
}
