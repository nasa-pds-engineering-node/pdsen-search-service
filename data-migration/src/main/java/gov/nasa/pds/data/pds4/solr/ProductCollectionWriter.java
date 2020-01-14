package gov.nasa.pds.data.pds4.solr;

import java.io.FileWriter;
import java.io.Writer;
import java.util.Set;
import java.util.TreeSet;

import gov.nasa.pds.data.pds4.model.ProductCollection;
import gov.nasa.pds.data.pds4.parser.ParserUtils;
import gov.nasa.pds.data.util.xml.SolrDocUtils;


public class ProductCollectionWriter
{
    private Writer writer;
    
    public ProductCollectionWriter(String path) throws Exception
    {
        writer = new FileWriter(path);
        writer.append("<add>\n");
    }

    
    public void close() throws Exception
    {
        writer.append("</add>\n");
        writer.close();
    }
    
    
    public void write(ProductCollection pc) throws Exception
    {
        writer.append("<doc>\n");

        SolrDocUtils.writeField(writer, "lid", pc.lid);
        SolrDocUtils.writeField(writer, "vid", pc.vid);
        SolrDocUtils.writeField(writer, "product_class", "Product_Collection");
        
        SolrDocUtils.writeField(writer, "title", pc.title);
        SolrDocUtils.writeField(writer, "description", pc.description);
        
        SolrDocUtils.writeField(writer, "collection_type", pc.type);
        SolrDocUtils.writeField(writer, "processing_level", pc.processingLevel);
        
        if(pc.purpose == null)
        {
            pc.purpose = "Science";
            System.out.println("Primary_Result_Summary/purpose is missing for " + pc.lid);
        }
        
        SolrDocUtils.writeField(writer, "purpose", pc.purpose);

        SolrDocUtils.writeField(writer, "science_facets", pc.scienceFacets);
        SolrDocUtils.writeField(writer, "science_facets", pc.keywords);
        
        writeInvestigation(writer, pc);
        writeInstrumentHost(writer, pc);
        writeInstruments(writer, pc);
        writeTargets(writer, pc);

        writer.append("</doc>\n");
    }

    
    private static void writeInvestigation(Writer writer, ProductCollection pc) throws Exception
    {
        if(pc.investigationRef == null) return;

        for(String ref: pc.investigationRef)
        {
            String shortLid = ParserUtils.getShortLid(ref);
            
            String id = ParserUtils.getInvestigationId(shortLid);
            if(id != null)
            {
                SolrDocUtils.writeField(writer, "investigation_id", id);
            }
        }
    }

    
    private static void writeInstrumentHost(Writer writer, ProductCollection pc) throws Exception
    {
        if(pc.instrumentHostRef == null) return;

        for(String ref: pc.instrumentHostRef)
        {
            String shortLid = ParserUtils.getShortLid(ref);
            
            String id = ParserUtils.getInstrumentHostId(shortLid);
            if(id != null)
            {
                SolrDocUtils.writeField(writer, "instrument_host_id", id);
            }
        }
    }

    
    private static void writeInstruments(Writer writer, ProductCollection pc) throws Exception
    {
        if(pc.instrumentRef == null) return;

        for(String ref: pc.instrumentRef)
        {
            String shortLid = ParserUtils.getShortLid(ref);

            String id = ParserUtils.getInstrumentId(shortLid);
            if(id != null)
            {
                SolrDocUtils.writeField(writer, "instrument_id", id);
            }
        }
    }
    
    
    private static void writeTargets(Writer writer, ProductCollection pc) throws Exception
    {
        if(pc.targetRef == null) return;

        Set<String> types = new TreeSet<>(); 
        
        for(String ref: pc.targetRef)
        {
            String shortLid = ParserUtils.getShortLid(ref);

            String[] tuple = ParserUtils.getTargetTuple(shortLid);
            if(tuple == null || tuple.length != 2)
            {
                System.out.println("WARNING: Invalid target reference: " + ref);
                continue;
            }
            
            String tgtType = tuple[0];
            String tgtName = tuple[1];
            
            types.add(tgtType);
            
            // TODO: FIX: It is a hack. Do dictionary lookup.
            if(tgtType.equals("asteroid"))
            {
                int idx = tgtName.indexOf('_');
                if(idx > 0)
                {
                    String name = tgtName.substring(idx+1);
                    SolrDocUtils.writeField(writer, "target_name", name);
                }
                else
                {
                    System.out.println("WARNING: Could not extract asterid name: " + tgtName);
                }
            }
            else
            {
                SolrDocUtils.writeField(writer, "target_name", tgtName);
            }
        }

        for(String type: types)
        {
            SolrDocUtils.writeField(writer, "target_type", type);
        }
    }

}
