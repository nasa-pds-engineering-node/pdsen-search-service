package gov.nasa.pds.data.pds3.solr;

import java.io.FileWriter;
import java.io.Writer;
import java.util.Set;

import gov.nasa.pds.data.util.FieldMap;
import gov.nasa.pds.data.util.xml.SolrDocUtils;

public class ProductCollectionWriterPds3
{
    private Writer writer;
    
    public ProductCollectionWriterPds3(String path) throws Exception
    {
        writer = new FileWriter(path);
        writer.append("<add>\n");
    }

    
    public void close() throws Exception
    {
        writer.append("</add>\n");
        writer.close();
    }

    
    public void write(FieldMap fields) throws Exception
    {
        writer.append("<doc>\n");

        SolrDocUtils.writeField(writer, "lid", fields.getFirstValue("identifier"));
        SolrDocUtils.writeField(writer, "vid", fields.getFirstValue("version_id"));
        SolrDocUtils.writeField(writer, "data_set_id", fields.getFirstValue("data_set_id"));
        SolrDocUtils.writeField(writer, "product_class", "Product_Data_Set_PDS3");
        
        SolrDocUtils.writeField(writer, "title", fields.getFirstValue("title"));
        //SolrDocUtils.writeField(writer, "description", pc.description);
        
        //SolrDocUtils.writeField(writer, "collection_type", pc.type);
        //SolrDocUtils.writeField(writer, "processing_level", pc.processingLevel);
        
        SolrDocUtils.writeField(writer, "purpose", "Science");
        
        writeTargets(fields);

        writer.append("</doc>\n");
    }
    
    
    private void writeTargets(FieldMap fields) throws Exception
    {
        Set<String> targetNames = fields.getValues("target_name");
        if(targetNames == null || targetNames.isEmpty())
        {
            System.out.println("WARNING: Target name(s) are missing for " + fields.getFirstValue("identifier"));
        }
        else
        {
            for(String tgtName: targetNames)
            {
                SolrDocUtils.writeField(writer, "target_name", tgtName.toLowerCase());
            }
        }
        
        Set<String> targetTypes = fields.getValues("target_type");
        if(targetTypes == null || targetTypes.isEmpty())
        {
            System.out.println("WARNING: Target type(s) are missing for " + fields.getFirstValue("identifier"));
        }
        else
        {
            for(String type: targetTypes)
            {
                SolrDocUtils.writeField(writer, "target_type", type.toLowerCase());
            }
        }
    }
}
