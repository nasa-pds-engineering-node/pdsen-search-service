package gov.nasa.pds.data.pds3.tools;

import java.io.FileWriter;
import java.io.Writer;
import gov.nasa.pds.data.util.FieldMap;
import gov.nasa.pds.data.util.xml.SolrDocParser;
import gov.nasa.pds.data.util.xml.SolrDocUtils;


public class Pds3ToSearch
{
    private static class DocCB implements SolrDocParser.Callback
    {
        private FieldMap fields;
        private Writer writer;
        
        public DocCB(Writer writer)
        {
            this.writer = writer;
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
                String lidvid = fields.getFirstValue("lid") + "::" + fields.getFirstValue("vid");
                fields.addValue("lidvid", lidvid);
                
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
            if(name.equals("identifier")) name = "lid";
            else if(name.equals("version_id")) name = "vid";
            else if(name.equals("start_time") || name.equals("stop_time")) value = value + ".000Z";
            
            fields.addValue(name, value);
        }
    }
    
    
    public static void processFile(String inPath, String outPath) throws Exception
    {
        Writer writer = new FileWriter(outPath);
        DocCB cb = new DocCB(writer);
        SolrDocParser parser = new SolrDocParser(inPath, cb);
        
        writer.write("<add>\n");
        parser.parse();
        writer.write("</add>\n");
        
        writer.close();
        parser.close();
    }

}
