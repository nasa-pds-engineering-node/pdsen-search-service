package gov.nasa.pds.search.solr;

import java.io.IOException;
import java.io.Writer;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.apache.solr.client.solrj.response.FacetField;
import org.apache.solr.client.solrj.response.QueryResponse;
import org.apache.solr.common.SolrDocument;
import org.apache.solr.common.SolrDocumentList;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.core.util.DefaultPrettyPrinter;
import com.fasterxml.jackson.databind.ObjectMapper;

import gov.nasa.pds.search.util.NameMapper;


/**
 * Writes Solr response in JSON format. 
 * @author karpenko
 */
public class QueryResponseJsonWriter
{
    private JsonGenerator jgen;
    private List<String> includeFields;
    private Set<String> excludeFields = new HashSet<>();
    private NameMapper nameMapper;
    
    /**
     * Constructor
     * @param out Output Stream for JSON.
     * @throws IOException
     */
    public QueryResponseJsonWriter(Writer writer) throws IOException
    {
        DateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS");

        ObjectMapper mapper = new ObjectMapper();
        mapper.setDateFormat(dateFormat);
        //mapper.configure(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS, false);

        jgen = mapper.getFactory().createGenerator(writer);
    }


    public void setPrettyPrint()
    {
        jgen.setPrettyPrinter(new DefaultPrettyPrinter());
    }
    
    
    /**
     * Set a list of fields to write.
     * @param fields A list of fields to write.
     */
    public void includeFields(List<String> fields)
    {
        this.includeFields = fields;
    }
    
    
    /**
     * Exclude these fields from the response.
     * @param fields
     */
    public void excludeFields(String... fields)
    {
        if(fields == null) return;
        
        for(String field: fields)
        {
            excludeFields.add(field);
        }
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
    public void write(QueryResponse qResp) throws IOException
    {
        jgen.writeStartObject(); // Root

        writeResponse(qResp);
        writeFacetCounts(qResp);
        
        jgen.writeEndObject(); // Root
        
        jgen.close();
    }
    
    
    private void writeResponse(QueryResponse qResp) throws IOException
    {
        SolrDocumentList docList = qResp.getResults();
        
        jgen.writeFieldName("response");
        jgen.writeStartObject(); // Response

        // Header
        jgen.writeStringField("status", "ok");
        jgen.writeNumberField("numFound", docList.getNumFound());
        jgen.writeNumberField("start", docList.getStart());
        jgen.writeNumberField("rows", docList.size());
        // Docs
        jgen.writeFieldName("docs"); writeDocs(docList);

        jgen.writeEndObject(); // Response
    }
    
    
    private void writeFacetCounts(QueryResponse qResp) throws IOException
    {
        List<FacetField> facets = qResp.getFacetFields();
        if(facets == null || facets.size() == 0) return;

        jgen.writeFieldName("facet_counts");
        jgen.writeStartObject();
        jgen.writeFieldName("facet_fields");
        jgen.writeStartObject();
        
        for(FacetField ff: facets)
        {
            jgen.writeFieldName(ff.getName());
            writeFacetField(ff);
        }

        jgen.writeEndObject();  // end facet_fields        
        jgen.writeEndObject();  // end facet_counts
    }
    
    
    private void writeFacetField(FacetField ff) throws IOException
    {
        jgen.writeStartArray();
        
        for(FacetField.Count cnt: ff.getValues())
        {
            jgen.writeString(cnt.getName());
            jgen.writeNumber(cnt.getCount());
        }
        
        jgen.writeEndArray();
    }
    
    
    private void writeDocs(SolrDocumentList docList) throws IOException
    {
        jgen.writeStartArray();
        
        for(SolrDocument doc: docList)
        {
            jgen.writeStartObject();
            
            // Use pre-configured field names if available.
            // Otherwise, get a list of fields for each document.
            List<String> fields = includeFields;
            if(fields == null)
            {
                fields = new ArrayList<>(doc.getFieldNames());
                Collections.sort(fields);
            }
            
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

        // Exclude fields
        if(includeFields == null && name.startsWith("_") && name.endsWith("_")) return;
        if(excludeFields.contains(name)) return;

        // Map field names
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
