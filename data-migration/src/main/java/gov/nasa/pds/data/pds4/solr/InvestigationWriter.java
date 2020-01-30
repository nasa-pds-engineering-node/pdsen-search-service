package gov.nasa.pds.data.pds4.solr;

import java.io.FileWriter;
import java.io.Writer;

import gov.nasa.pds.data.pds4.model.Investigation;
import gov.nasa.pds.data.util.xml.SolrDocUtils;


public class InvestigationWriter
{
    private Writer writer;

    public InvestigationWriter(String path) throws Exception
    {
        writer = new FileWriter(path);
        writer.append("<add>\n");
    }

    
    public void close() throws Exception
    {
        writer.append("</add>\n");
        writer.close();
    }


    public void write(Investigation inv) throws Exception
    {
        writer.append("<doc>\n");

        SolrDocUtils.writeField(writer, "id", inv.shortLid);
        SolrDocUtils.writeField(writer, "title", inv.title);
        SolrDocUtils.writeField(writer, "investigation_type", inv.type);

        SolrDocUtils.writeField(writer, "lid", inv.lid);
        SolrDocUtils.writeField(writer, "investigation_id", inv.id);
        SolrDocUtils.writeField(writer, "instrument_host_id", inv.hostIds);

        
        writeTargets(inv);
        
        writer.append("</doc>\n");
    }
    
    
    private void writeTargets(Investigation data) throws Exception
    {
        if(data.targetIds != null && !data.targetIds.isEmpty())
        {
            SolrDocUtils.writeField(writer, "target_id", data.targetIds.toArray(new String[0]));
        }

        if(data.targetTypes != null && !data.targetTypes.isEmpty())
        {
            SolrDocUtils.writeField(writer, "target_type", data.targetTypes.toArray(new String[0]));
        }
    }

}
