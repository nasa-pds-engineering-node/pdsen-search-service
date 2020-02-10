package gov.nasa.pds.data.pds3.solr;

import java.io.FileWriter;
import java.io.Writer;
import java.util.Collection;

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
        writeField("description", data.description);
        
        SolrDocUtils.writeField(writer, "collection_type", data.type);
        writeProcessingLevels(data);
        
        SolrDocUtils.writeField(writer, "purpose", data.purpose);
        writeField("science_facets", data.scienceFacets);
        
        writeField("investigation_id", data.investigationIds);
        writeField("instrument_host_id", data.instrumentHostIds);
        writeField("instrument_id", data.instrumentIds);
        writeTargets(data);

        writer.append("</doc>\n");
    }
    

    private void writeField(String name, Collection<String> value) throws Exception
    {
        if(value != null && !value.isEmpty())
        {
            SolrDocUtils.writeField(writer, name, value.toArray(new String[0]));
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
