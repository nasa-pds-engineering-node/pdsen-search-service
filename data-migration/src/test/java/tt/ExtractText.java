package tt;

import java.io.FileWriter;
import java.io.Writer;

import gov.nasa.pds.data.util.xml.SolrDocParser;

public class ExtractText
{

    private static class TextCB implements SolrDocParser.Callback
    {
        private Writer writer;
        
        public TextCB(String fileName) throws Exception
        {
            this.writer = new FileWriter(fileName);
        }
        
        public void close() throws Exception
        {
            writer.close();
        }
        
        @Override
        public void onDocStart()
        {
        }

        @Override
        public boolean onDocEnd()
        {
            return true;
        }

        @Override
        public void onField(String name, String value)
        {
            try
            {
                if(name.equals("description") || name.equals("data_set_terse_description")
                        || name.equals("abstract_text") || name.equals("data_set_description"))
                {
                    if(value != null)
                    {
                        writer.write(value);
                        writer.write("\n");
                    }
                }
            }
            catch(Exception ex)
            {
                ex.printStackTrace();
            }
        }
        
    }
    
    
    public static void main(String[] args) throws Exception
    {
        TextCB cb = new TextCB("/tmp/ds8.txt");
        
        SolrDocParser parser = new SolrDocParser("/tmp/pds3-ds-8.xml", cb);
        parser.parse();
        parser.close();
        cb.close();
    }
    
}
