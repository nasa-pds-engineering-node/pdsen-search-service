package gov.nasa.pds.data.pds4.tools;

import java.util.HashSet;
import java.util.Set;

import gov.nasa.pds.data.pds4.model.Investigation;
import gov.nasa.pds.data.pds4.solr.InvestigationWriter;
import gov.nasa.pds.data.util.FieldMap;
import gov.nasa.pds.data.util.xml.SolrDocParser;


public class SolrDumpProcessor_Investigation
{
    private static class CleanCB implements SolrDocParser.Callback
    {
        private FieldMap fields;
        private Set<String> ignoreFields;
        
        private InvestigationProcessor iproc;
        private InvestigationWriter writer;
        
        public int counter;
        
        
        public CleanCB(InvestigationProcessor iproc, String outPath) throws Exception
        {
            this.iproc = iproc;
            this.writer = new InvestigationWriter(outPath);
            
            ignoreFields = new HashSet<>();
            
            ignoreFields.add("objectType");
            ignoreFields.add("data_class");
            ignoreFields.add("product_class");
            ignoreFields.add("data_product_type");
            ignoreFields.add("external_reference_text");
            
            ignoreFields.add("modification_date");
            ignoreFields.add("modification_description");
            
            ignoreFields.add("file_ref_name");
            ignoreFields.add("file_ref_size");
            ignoreFields.add("file_ref_location");
            ignoreFields.add("file_ref_url");
            ignoreFields.add("resLocation");
            
            ignoreFields.add("investigation_description");
            ignoreFields.add("timestamp");
            ignoreFields.add("score");
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

            fields.addValue(name, value);            
        }
        
        
        @Override
        public boolean onDocEnd()
        {
            try
            {
                String investigationType = fields.getFirstValue("investigation_type"); 
                if(investigationType == null || !investigationType.equalsIgnoreCase("Mission")) return true;
                
                String title = fields.getFirstValue("title");
                if(title == null || title.equalsIgnoreCase("PRE-MAGELLAN")) return true;
                
                Investigation data = iproc.process(fields);
                writer.write(data);
                
                counter++;                
            }
            catch(Exception ex)
            {
                ex.printStackTrace();
                return false;
            }
            
            return true;
        }
    }

    
    ////////////////////////////////////////////////////////////////////////////////////////    
    
    private InvestigationProcessor iproc;
    
    public SolrDumpProcessor_Investigation() throws Exception
    {
        this.iproc = new InvestigationProcessor();
    }
    
    public void processFile(String inPath, String outPath) throws Exception
    {
        CleanCB cb = new CleanCB(iproc, outPath);
        SolrDocParser parser = new SolrDocParser(inPath, cb);
        parser.parse();
        parser.close();
        cb.close();
        
        System.out.println(cb.counter);
    }

}
