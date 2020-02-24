package gov.nasa.pds.data.pds3.tools;

import gov.nasa.pds.data.pds3.model.Pds3Resource;
import gov.nasa.pds.data.pds3.solr.ResInvestWriter;
import gov.nasa.pds.data.util.FieldMap;
import gov.nasa.pds.data.util.xml.SolrDocParser;


public class SolrDumpProcessor_ResInvest
{
    private static class CleanCB implements SolrDocParser.Callback
    {
        private Pds3ResourceProcessor rsp;
        private FieldMap fields;
        private ResInvestWriter writer;
        
        
        public CleanCB(Pds3ResourceProcessor rsp, String outPath) throws Exception
        {
            this.rsp = rsp;
            this.writer = new ResInvestWriter(outPath);
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
            if(name.equals("identifier") || name.equals("version_id")
                    || name.equals("resource_url") || name.equals("resource_type")
                    || name.equals("investigation_ref") || name.equals("investigation_name"))
            {
                fields.addValue(name, value);
            }
        }
        
        
        @Override
        public boolean onDocEnd()
        {
            try
            {
                Pds3Resource res = rsp.process(fields);
                writer.write(res);
                return true;
            }
            catch(Exception ex)
            {
                ex.printStackTrace();
                return false;
            }
        }
    }

    
    ///////////////////////////////////////////////////////////////
    
    private Pds3ResourceProcessor rsp;
    
    
    public SolrDumpProcessor_ResInvest() throws Exception
    {
        rsp = new Pds3ResourceProcessor();
    }
    
    
    public void processFile(String inPath, String outPath) throws Exception
    {
        CleanCB cb = new CleanCB(rsp, outPath);
        SolrDocParser parser = new SolrDocParser(inPath, cb);
        parser.parse();
        parser.close();
        cb.close();
    }

}
