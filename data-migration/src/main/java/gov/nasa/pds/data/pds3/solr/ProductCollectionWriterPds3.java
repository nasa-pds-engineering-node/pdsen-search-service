package gov.nasa.pds.data.pds3.solr;

import java.io.FileWriter;
import java.io.Writer;
import gov.nasa.pds.data.pds3.model.Pds3DataCollection;
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

    
    public void write(Pds3DataCollection data) throws Exception
    {
        writer.append("<doc>\n");

        SolrDocUtils.writeField(writer, "lid", data.lid);
        SolrDocUtils.writeField(writer, "vid", data.vid);
        SolrDocUtils.writeField(writer, "data_set_id", data.datasetId);
        SolrDocUtils.writeField(writer, "product_class", "Product_Data_Set_PDS3");
        
        SolrDocUtils.writeField(writer, "title", data.title);
        writeDescription(data);
        
        SolrDocUtils.writeField(writer, "collection_type", data.type);
        writeProcessingLevels(data);
        
        SolrDocUtils.writeField(writer, "purpose", data.purpose);
        
        writeInvestigations(data);
        writeInstrumentHosts(data);
        writeInstruments(data);
        writeTargets(data);

        writer.append("</doc>\n");
    }
    

    private void writeDescription(Pds3DataCollection data) throws Exception
    {
        if(data.description != null && !data.description.isEmpty())
        {
            SolrDocUtils.writeField(writer, "description", data.description.toArray(new String[0]));
        }
    }
    
    
    private void writeProcessingLevels(Pds3DataCollection data) throws Exception
    {
        if(data.processingLevels != null && !data.processingLevels.isEmpty())
        {
            SolrDocUtils.writeField(writer, "processing_level", data.processingLevels.toArray(new String[0]));
        }
        
        if(data.codmacLevels != null && !data.codmacLevels.isEmpty())
        {
            SolrDocUtils.writeField(writer, "codmac_level", data.codmacLevels.toArray(new String[0]));
        }
    }

    
    private void writeInvestigations(Pds3DataCollection data) throws Exception
    {
        if(data.investigationIds == null || data.investigationIds.isEmpty()) return;
        
        SolrDocUtils.writeField(writer, "investigation_id", data.investigationIds.toArray(new String[0]));
    }


    private void writeInstrumentHosts(Pds3DataCollection data) throws Exception
    {
        if(data.instrumentHostIds == null || data.instrumentHostIds.isEmpty()) return;
        
        SolrDocUtils.writeField(writer, "instrument_host_id", data.instrumentHostIds.toArray(new String[0]));
    }

    
    private void writeInstruments(Pds3DataCollection data) throws Exception
    {
        if(data.instrumentIds == null || data.instrumentIds.isEmpty()) return;
        
        SolrDocUtils.writeField(writer, "instrument_id", data.instrumentIds.toArray(new String[0]));
    }

    
    private void writeTargets(Pds3DataCollection data) throws Exception
    {
        if(data.targetNames != null && !data.targetNames.isEmpty())
        {
            SolrDocUtils.writeField(writer, "target_id", data.targetNames.toArray(new String[0]));
        }

        if(data.targetTypes != null && !data.targetTypes.isEmpty())
        {
            SolrDocUtils.writeField(writer, "target_type", data.targetTypes.toArray(new String[0]));
        }
    }
    
}
