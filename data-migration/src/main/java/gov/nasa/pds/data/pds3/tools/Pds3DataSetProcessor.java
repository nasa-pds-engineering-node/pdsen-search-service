package gov.nasa.pds.data.pds3.tools;

import java.util.Map;
import java.util.Set;

import gov.nasa.pds.data.pds3.model.Pds3DataCollection;
import gov.nasa.pds.data.util.FieldMap;
import gov.nasa.pds.data.util.MapUtils;


public class Pds3DataSetProcessor
{
    Map<String, String> investMap;
    Map<String, String> targetMap;
    
    
    public Pds3DataSetProcessor() throws Exception
    {
        investMap = MapUtils.loadMap("src/main/data/pds3/invest_name2id.txt");
        targetMap = MapUtils.loadMap("src/main/data/pds3/target_name2id.txt");
    }
    

    public String getInvestigationIdByName(String name)
    {
        return investMap.get(name);
    }
    
    
    public Pds3DataCollection process(FieldMap fields)
    {        
        Pds3DataCollection data = new Pds3DataCollection();
        
        data.lid = fields.getFirstValue("identifier");
        data.vid = fields.getFirstValue("version_id");
        data.datasetId = fields.getFirstValue("data_set_id");
        
        data.title = fields.getFirstValue("title");
        //SolrDocUtils.writeField(writer, "description", pc.description);
        
        //SolrDocUtils.writeField(writer, "collection_type", pc.type);
        //SolrDocUtils.writeField(writer, "processing_level", pc.processingLevel);
        
        data.purpose = "Science";
        
        Set<String> invest = fields.getValues("investigation_id"); 
        data.investigationId = invest.toArray(new String[0]);
        
        return data; 
    }
}
