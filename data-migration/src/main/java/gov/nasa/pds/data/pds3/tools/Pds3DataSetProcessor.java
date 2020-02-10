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
    private Map<String, String> investMap;
    private Map<String, String> targetMap;

    private Pds3DataClassifier classifier;
    
    
    public Pds3DataSetProcessor() throws Exception
    {
        investMap = MapUtils.loadMap("src/main/data/pds3/invest_name2id.txt");
        targetMap = MapUtils.loadMap("src/main/data/pds3/target_name2id.txt");                
        classifier = new Pds3DataClassifier("src/main/data/pds3/classifier");
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

        classifyData(data, fields);
        
        return data; 
    }

        
    private void classifyData(Pds3DataCollection data, FieldMap fields)
    {
        classifyDataByInstrument(data, fields);
    }
    
    
    private void classifyDataByInstrument(Pds3DataCollection data, FieldMap fields)
    {
        if("spice_kernel".equals(data.collectionType) || data.instrumentIds.contains("SPICE"))
        {
            data.scienceFacets.add("Navigation");
            data.scienceFacets.add("Observation Geometry");
            return;
        }
        
        // Instrument type
        Set<String> types = fields.getValues("instrument_type");
        if(types == null || types.isEmpty())
        {
            if(data.targetTypes != null && data.targetTypes.contains("dust"))
            {
                data.scienceFacets.add("Dust");
            }
            else
            {
                System.out.println("WARNING: Missing instrument type. (" + data.lid + ")");
            }
        }
        else
        {
            for(String itype: types)
            {
                String cl = classifier.classifyInstrumentType(itype);
                if(cl == null) 
                {
                    System.out.println("WARNING: Could not classify instrument type " + itype + " (" + data.lid + ")"); 
                }
                else
                {
                    data.scienceFacets.add(cl);
                }
            }
        }
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
        
        descr = fields.getFirstValue("data_set_description");
        if(descr != null) 
        {
            if(descr.startsWith("Data Set Overview ================= ")) descr = descr.substring(36); 
            if(descr.length() > 255) descr = descr.substring(0, 255);
            data.description.add(descr);
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
        
        String[] tokens = (datasetId.indexOf('-') > 0) ? datasetId.split("-") : datasetId.split("_");
        if(tokens.length < 4)
        {
            System.out.println("WARNING: Could not extract CODMAC level from data_set_id " + datasetId);
            return;
        }
        
        // CODMAC levels separated by /
        String tmp = tokens[3];
        if(datasetId.startsWith("CO-SR-UVIS-HSP")) tmp = tokens[4];
        
        tokens = tmp.split("/");
        
        for(String codmacLevel: tokens)
        {
            String pds4Level = codmacToPds4(codmacLevel);
            if(pds4Level == null)
            {
                System.out.println("WARNING: Invalid CODMAC level: " + codmacLevel 
                        + " (data_set_id: " + datasetId + ")");
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
            if(tuple == null || tuple.length < 2 || tuple.length > 3)
            {
                System.out.println("WARNING: Invalid target id: " + id);
                continue;
            }
            
            String tgtType = tuple[0];
            String tgtName = tuple[tuple.length-1];

            data.targetNames.add(tgtName);
            data.targetTypes.add(tgtType);
        }
    }

}
