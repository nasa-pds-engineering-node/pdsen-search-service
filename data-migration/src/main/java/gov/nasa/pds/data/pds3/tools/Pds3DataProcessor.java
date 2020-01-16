package gov.nasa.pds.data.pds3.tools;

import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import org.apache.commons.lang.StringUtils;

import gov.nasa.pds.data.pds3.solr.ProductCollectionWriterPds3;
import gov.nasa.pds.data.util.FieldMap;
import gov.nasa.pds.data.util.xml.SolrDocParser;


public class Pds3DataProcessor
{
    private static class CleanCB implements SolrDocParser.Callback
    {
        private FieldMap fields;
        private ProductCollectionWriterPds3 writer;

        private Set<String> ignoreFields;
        private Set<String> doNotCleanText;

        private Map<String, String> missionName2Id;

        public int counter;

        
        public CleanCB(Map<String, String> missionMap, String outPath) throws Exception
        {
            this.missionName2Id = missionMap;
            this.writer = new ProductCollectionWriterPds3(outPath);
            
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
            
            // Temporary
            ignoreFields.add("confidence_level_note");
            ignoreFields.add("data_set_description");
            ignoreFields.add("archive_status");
            ignoreFields.add("external_reference_text");
            ignoreFields.add("pds_model_version");
            ignoreFields.add("citation_description");
            ignoreFields.add("resLocation");
            ignoreFields.add("resource_ref");
            
            ignoreFields.add("start_time");
            ignoreFields.add("stop_time");
            ignoreFields.add("data_set_release_date");
            
            // Text normalization
            doNotCleanText = new HashSet<>();
            doNotCleanText.add("data_set_description");
            doNotCleanText.add("confidence_level_note");
        }

        
        public void close() throws Exception
        {
            writer.close();
        }
        
        @Override
        public void onDocStart()
        {
            fields = new FieldMap();
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
            
            // Replace investigation_name with investigation_id
            if(name.equals("investigation_name"))
            {
                String id = getMissionId(value);
                fields.addValue("investigation_id", id);
                return;
            }
            
            fields.addValue(name, value);
        }
        
        
        @Override
        public boolean onDocEnd()
        {
            try
            {
                String investigationId = fields.getFirstValue("investigation_id"); 
                if(investigationId == null || !investigationId.equalsIgnoreCase("dawn")) return true;

                counter++;
                writer.write(fields);
            }
            catch(Exception ex)
            {
                ex.printStackTrace();
                return false;
            }
            
            return true;
        }

        
        private String getMissionId(String value)
        {
            String id = missionName2Id.get(value);
            if(id == null)
            {
                id = value.toLowerCase();
                System.out.println("WARNING: " + value);
            }

            return id;
        }
        
        
    }
    
    ////////////////////////////////////////////////////////////////////////////////////////    
    
    public static void processFile(String inPath, String outPath, Map<String, String> missionMap) throws Exception
    {
        CleanCB cb = new CleanCB(missionMap, outPath);
        SolrDocParser parser = new SolrDocParser(inPath, cb);
        parser.parse();
        parser.close();
        cb.close();
        
        System.out.println(cb.counter);
    }

}
