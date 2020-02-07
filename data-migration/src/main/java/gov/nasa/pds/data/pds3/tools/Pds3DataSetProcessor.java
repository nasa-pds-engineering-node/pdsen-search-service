package gov.nasa.pds.data.pds3.tools;

import java.util.ArrayList;
import java.util.Map;
import java.util.Set;
import java.util.TreeSet;

import gov.nasa.pds.data.pds3.model.Pds3DataCollection;
import gov.nasa.pds.data.pds4.parser.ParserUtils;
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
        
        processDescription(data, fields);
        
        extractCollectionType(data, fields);
        extractProcessingLevels(data, fields);
        extractPurpose(data, fields);
        
        data.investigationIds = fields.getValues("investigation_id");
        
        processInstrumentHost(data, fields);
        processInstruments(data, fields);
        processTargets(data, fields);
        
        return data; 
    }

    
    private void processDescription(Pds3DataCollection data, FieldMap fields)
    {
        data.description = new ArrayList<String>();
        
        String terse = fields.getFirstValue("data_set_terse_description");
        if(terse != null && !terse.isEmpty()) data.description.add(terse);
        
        // Usually data_set_terse_description == description 
        String descr = fields.getFirstValue("description");
        if(descr != null && !descr.equals(terse)) data.description.add(descr);

        String abstr = fields.getFirstValue("abstract_text");
        if(abstr != null && !abstr.isEmpty()) 
        {
            if(abstr.startsWith("Abstract ========")) abstr = abstr.substring(18);
            else if(abstr.startsWith("Abstract ")) abstr = abstr.substring(9);
            data.description.add(abstr);
        }
    }
    
    
    private void processInstrumentHost(Pds3DataCollection data, FieldMap fields)
    {
        Set<String> ihIds = fields.getValues("instrument_host_id");
        if(ihIds == null)
        {
            System.out.println("WARNING: No instrument host id for " + data.lid);
            return;
        }
        
        if(ihIds.size() > 1)
        {
            System.out.println("WARNING: Multiple instrument host ids for " + data.lid);
        }
        
        data.instrumentHostIds = ihIds;
    }

    
    private void processInstruments(Pds3DataCollection data, FieldMap fields)
    {
        data.instrumentIds = fields.getValues("instrument_id");
    }

    
    private void extractCollectionType(Pds3DataCollection data, FieldMap fields)
    {
        Set<String> instrumentIds = fields.getValues("instrument_id");
        if(instrumentIds != null && instrumentIds.contains("SPICE"))
        {
            data.type = "spice_kernel";
            return;
        }
        
        data.type = "data";        
    }

    
    private void extractProcessingLevels(Pds3DataCollection data, FieldMap fields)
    {
        String datasetId = fields.getFirstValue("data_set_id");
        
        data.processingLevels = new TreeSet<>();
        data.codmacLevels = new TreeSet<>();
        
        String[] tokens = datasetId.split("-");
        // CODMAC levels separated by /
        String tmp = tokens[3];
        tokens = tmp.split("/");
        
        for(String codmacLevel: tokens)
        {
            String pds4Level = codmacToPds4(codmacLevel);
            if(pds4Level == null)
            {
                System.out.println("WARNING: Invalid CODMAC level: " + codmacLevel);
            }
            else
            {
                data.codmacLevels.add(codmacLevel);
                data.processingLevels.add(pds4Level);
            }
        }
    }

    
    private void extractPurpose(Pds3DataCollection data, FieldMap fields)
    {
        String datasetId = fields.getFirstValue("data_set_id");
        String[] tokens = datasetId.split("-");
        if(tokens.length > 1 && "CAL".equals(tokens[1]))
        {
            data.purpose = "Calibration";
        }
        else
        {
            data.purpose = "Science";
        }
    }
    
    
    private String codmacToPds4(String codmac)
    {
        switch(codmac)
        {
        case "1":
            return "raw";
        case "2":                   // EDR
            return "raw";
        case "3":                   // RDR
            return "calibrated";
        case "4":
            return "derived";
        case "5":
            return "derived";
        case "6":                   // SPICE Kernels
            return "derived";
        }
        
        return null;
    }
    
    
    private void processTargets(Pds3DataCollection data, FieldMap fields)
    {
        Set<String> targetNames = fields.getValues("target_name");
        if(targetNames == null || targetNames.isEmpty())
        {
            // NOTE: Some calibration data don't have targets.
            //System.out.println("WARNING: Target name(s) are missing for " + fields.getFirstValue("identifier"));
            return;
        }

        data.targetNames = new TreeSet<>();
        data.targetTypes = new TreeSet<>();
        
        for(String name: targetNames)
        {
            name = name.toLowerCase();
            String id = targetMap.get(name);
            
            String[] tuple = ParserUtils.getTargetTuple(id);
            if(tuple == null || tuple.length != 2)
            {
                System.out.println("WARNING: Invalid target id: " + id);
                continue;
            }
            
            String tgtType = tuple[0];
            String tgtName = tuple[1];

            data.targetNames.add(tgtName);
            data.targetTypes.add(tgtType);
        }
    }

}
