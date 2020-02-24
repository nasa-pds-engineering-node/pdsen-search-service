package gov.nasa.pds.data.pds3.solr;

import java.io.FileWriter;
import java.io.Writer;
import java.util.Collection;

import gov.nasa.pds.data.pds3.model.Pds3Resource;
import gov.nasa.pds.data.util.FieldMap;
import gov.nasa.pds.data.util.xml.SolrDocUtils;


public class ResInvestWriter
{
    private Writer writer;
    
    public ResInvestWriter(String path) throws Exception
    {
        writer = new FileWriter(path);
        writer.append("<add>\n");
    }

    
    public void close() throws Exception
    {
        writer.append("</add>\n");
        writer.close();
    }

    
    public void write(Pds3Resource res) throws Exception
    {
        writer.append("<doc>\n");

        SolrDocUtils.writeField(writer, "lid", res.lid);
        SolrDocUtils.writeField(writer, "vid", res.vid);

        SolrDocUtils.writeField(writer, "investigation_name", res.investigationName);
        writeField("investigation_id", res.investigationIds);
        
        SolrDocUtils.writeField(writer, "resource_type", res.resourceType);
        SolrDocUtils.writeField(writer, "resource_url", res.resourceUrl);
        
        writer.append("</doc>\n");
    }
    
    
    private void writeField(String name, Collection<String> value) throws Exception
    {
        if(value != null && !value.isEmpty())
        {
            SolrDocUtils.writeField(writer, name, value.toArray(new String[0]));
        }
    }
}
