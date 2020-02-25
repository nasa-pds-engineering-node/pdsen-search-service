package gov.nasa.pds.data.pds3.tools;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

import org.apache.commons.lang.StringUtils;

import gov.nasa.pds.data.pds3.model.Pds3Resource;
import gov.nasa.pds.data.pds4.parser.ParserUtils;
import gov.nasa.pds.data.util.FieldMap;
import gov.nasa.pds.data.util.MapUtils;


public class Pds3ResourceProcessor
{
    private Map<String, String> investMap = new HashMap<>(100);
    
    
    public Pds3ResourceProcessor() throws Exception
    {
        MapUtils.loadMap(investMap, "src/main/data/pds3/invest_name2id.txt");
    }
    
        
    public Pds3Resource process(FieldMap fields)
    {
        Pds3Resource data = new Pds3Resource();
        
        data.lid = fields.getFirstValue("identifier");
        data.vid = fields.getFirstValue("version_id");
        
        data.resourceType = fields.getFirstValue("resource_type");
        data.resourceUrl = fields.getFirstValue("resource_url");
                
        processInvestigation(data, fields);
        
        return data;
    }
    
    
    private void processInvestigation(Pds3Resource data, FieldMap fields)
    {
        data.investigationName = fields.getFirstValue("investigation_name");
        if(data.investigationName == null)
        {
            String shortLid = ParserUtils.getShortLid(fields.getFirstValue("investigation_ref"));
            data.investigationName = ParserUtils.getInvestigationId(shortLid);
            
            if(data.investigationName == null)
            {
                System.out.println("WARNING: Missing investigation name. Lid = " + data.lid);
                return;
            }
            else
            {
                data.investigationName = StringUtils.capitalize(data.investigationName);
            }
        }
        
        
        String id = getInvestigationIdByName(data.investigationName);
        if(id == null)
        {
            System.out.println("WARNING: Unknown investigation name: " + data.investigationName);
            data.investigationIds.add(data.investigationName.toLowerCase());
        }
        else
        {
            String[] tokens = id.split(",");
            Collections.addAll(data.investigationIds, tokens);
        }        
    }
    
    
    private String getInvestigationIdByName(String name)
    {
        if(name == null) return null;        
        return investMap.get(name);
    }
}
