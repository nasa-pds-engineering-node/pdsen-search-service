package gov.nasa.pds.search.ner;

import java.io.IOException;
import java.io.Writer;
import java.util.List;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.ObjectMapper;

import gov.nasa.pds.nlp.ner.NerToken;
import gov.nasa.pds.nlp.ner.NerTokenType;


/**
 * Writes NamedEntityRecognizer response in JSON format. 
 * @author karpenko
 */
public class NerJsonWriter
{
    private JsonGenerator jgen;
    
    /**
     * Constructor
     * @param out Output Stream for JSON.
     * @throws IOException
     */
    public NerJsonWriter(Writer writer) throws IOException
    {
        ObjectMapper mapper = new ObjectMapper();
        jgen = mapper.getFactory().createGenerator(writer);
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
     * Write a list of NER tokens.
     * @param tokens
     * @throws IOException
     */
    public void write(List<NerToken> tokens) throws IOException
    {
        jgen.writeStartObject(); // Root

        jgen.writeFieldName("response");
        jgen.writeStartObject(); // Response
        {
            // Header
            jgen.writeStringField("status", "ok");
            jgen.writeNumberField("rows", tokens.size());
            // Docs
            jgen.writeFieldName("docs"); writeDocs(tokens);
        }
        jgen.writeEndObject(); // Response
        
        jgen.writeEndObject(); // Root
        
        jgen.close();
    }
    
    
    private void writeDocs(List<NerToken> tokens) throws IOException
    {
        jgen.writeStartArray();
        
        for(NerToken token: tokens)
        {
            // Skip unknown tokens
            if(token.getType() == NerTokenType.UNKNOWN) continue;
            
            jgen.writeStartObject();
            
            // Text
            writeField("text", token.getKey());
            
            // Type 
            writeTokenTypeField(token);
            
            // ID
            String id = token.getId();
            if(id == null && token.getType() != 0) id = token.getKey();            
            writeField("id", id);
            
            jgen.writeEndObject();
        }
        
        jgen.writeEndArray();
    }

    
    private void writeField(String name, String value) throws IOException
    {
        if(value == null) return;
        jgen.writeStringField(name, value);
    }
    
    
    private void writeTokenTypeField(NerToken token) throws IOException
    {
        jgen.writeFieldName("type");

        if(token.getType() == NerTokenType.MULTIPLE)
        {
            jgen.writeStartArray();            
            for(int val: token.getAllTypes())
            {
                jgen.writeString(getTypeString(val));
            }            
            jgen.writeEndArray();
        }
        else
        {
            jgen.writeString(getTypeString(token.getType()));
        }        
    }
        
    
    private String getTypeString(int typeId)
    {
        switch(typeId)
        {
        case NerTokenType.TARGET:
            return "target";
        case NerTokenType.TARGET_TYPE:
            return "target_type";
        case NerTokenType.INSTRUMENT:
            return "instrument";
        case NerTokenType.INSTRUMENT_HOST:
            return "instrument_host";
        case NerTokenType.INVESTIGATION:
            return "investigation";
        default:
            return "unknown";
        }
    }
}
