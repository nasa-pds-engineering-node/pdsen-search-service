package gov.nasa.pds.data.pds3.tools;

import java.io.FileWriter;
import java.io.Writer;
import java.util.HashSet;
import java.util.Set;

import org.apache.commons.lang.StringUtils;

import gov.nasa.pds.data.util.FieldMap;
import gov.nasa.pds.data.util.xml.SolrDocParser;
import gov.nasa.pds.data.util.xml.SolrDocUtils;

public class Pds3DataCleaner
{
    private static class CleanCB implements SolrDocParser.Callback
    {
        private FieldMap fields;
        private Writer writer;

        private Set<String> ignoreFields;
        private Set<String> doNotCleanText;
        
        public CleanCB(Writer writer)
        {
            this.writer = writer;
            
            // Ignore fields
            ignoreFields = new HashSet<>();
            
            ignoreFields.add("objectType");
            ignoreFields.add("data_product_type");
            ignoreFields.add("data_object_type");
            
            ignoreFields.add("modification_date");
            ignoreFields.add("modification_description");
            
            ignoreFields.add("investigation_start_date");
            ignoreFields.add("investigation_stop_date");

            ignoreFields.add("full_name");            
            
            ignoreFields.add("search_id");
            ignoreFields.add("timestamp");
            ignoreFields.add("score");
            
            // Text normalization
            doNotCleanText = new HashSet<>();
            doNotCleanText.add("data_set_description");
            doNotCleanText.add("confidence_level_note");
        }
        
        @Override
        public void onDocStart()
        {
            fields = new FieldMap();
        }

        @Override
        public boolean onDocEnd()
        {
            try
            {
                SolrDocUtils.writeFieldMap(writer, fields);
            }
            catch(Exception ex)
            {
                ex.printStackTrace();
                return false;
            }
            
            return true;
        }

        @Override
        public void onField(String name, String value)
        {
            // Ignore fields
            if(name.startsWith("form-")) return;
            if(ignoreFields.contains(name)) return;
            
            if(value.equalsIgnoreCase("null")) return;
            if(value.equalsIgnoreCase("unknown")) return;
            if(value.equalsIgnoreCase("unk")) return;
            
            // Normalize spaces (remove end of lines and repeating spaces)
            if(!doNotCleanText.contains(name))
            {
                value = StringUtils.normalizeSpace(value);
            }
            
            fields.addValue(name, value);
        }
    }
    
    
    public static void processFile(String inPath, String outPath) throws Exception
    {
        Writer writer = new FileWriter(outPath);
        CleanCB cb = new CleanCB(writer);
        SolrDocParser parser = new SolrDocParser(inPath, cb);
        
        writer.write("<add>\n");
        parser.parse();
        writer.write("</add>\n");
        
        writer.close();
        parser.close();
    }

}
