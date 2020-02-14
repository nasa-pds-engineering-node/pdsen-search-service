package gov.nasa.pds.data.pds3.tools;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import java.util.TreeMap;
import java.util.TreeSet;

import gov.nasa.pds.data.pds3.model.Pds3DataCollection;
import gov.nasa.pds.data.pds4.parser.ParserUtils;
import gov.nasa.pds.data.util.FieldMap;
import gov.nasa.pds.data.util.MapUtils;


public class Pds3DataSetProcessor
{
    private Map<String, String> investMap = new HashMap<>(100);
    private Map<String, String> targetMap = new HashMap<>(1000);

    private Pds3DataClassifier classifier;
    
    
    public Pds3DataSetProcessor() throws Exception
    {
        MapUtils.loadMap(investMap, "src/main/data/pds3/invest_name2id.txt");
        MapUtils.loadMap(targetMap, "src/main/data/pds3/target_name2id.txt");
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
        classifyDataByDescription(data, fields);
    }
    
    
    private void classifyDataByInstrument(Pds3DataCollection data, FieldMap fields)
    {
        if("spice_kernel".equals(data.collectionType))
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
                Set<String> keywords = classifier.extractKeywords(itype);
                if(keywords == null) 
                {
                    System.out.println("WARNING: Could not classify instrument type " + itype + " (" + data.lid + ")"); 
                }
                else
                {
                    data.scienceFacets.addAll(keywords);
                }
            }
        }
    }
    

    private void classifyDataByDescription(Pds3DataCollection data, FieldMap fields)
    {
        if(data.description == null) return;
        
        for(String text: data.description)
        {
            Set<String> keywords = classifier.extractKeywords(text);
            if(keywords != null)
            {
                data.scienceFacets.addAll(keywords);
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
            String datasetId = fields.getFirstValue("identifier");
            int idx = datasetId.indexOf(":data_set:data_set.");
            datasetId = datasetId.substring(idx+19);
            
            String[] tokens = datasetId.split("-");
            String id = tokens[0];
            
            if(id.equals("mex") || id.equals("gio") || id.equals("hp") || id.equals("hst") || id.equals("vg2"))
            {
                ihIds = new TreeSet<>();
                ihIds.add(id);
            }
            else
            {
                System.out.println("WARNING: No instrument host id for " + data.lid);
                return;
            }
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
        if(data.instrumentIds == null)
        {
            System.out.println("WARNING: Missing instrument id (" + data.lid + ")");
        }
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
        // Fix invalid data
        if(datasetId.startsWith("CO-SR-UVIS-HSP") 
                || datasetId.startsWith("MGS-M-MOC-NA/WA")
                || datasetId.startsWith("MSL-M-CHEMCAM-")
                || datasetId.startsWith("CH1-ORB-L-M3-")
                || datasetId.startsWith("LCROSS-E/L-NSP1-FL-")
                || datasetId.startsWith("LCROSS-X-NSP2-FL-")
                || datasetId.startsWith("CH1-ORB-L-MRFFR-"))
        {
            tmp = tokens[4];
        }
        else if(datasetId.startsWith("NEAR-A-5-") 
                || datasetId.startsWith("NEAR-MSI-6-")
                || datasetId.startsWith("LP-L-6-")
                || datasetId.startsWith("JNO-SS-3-")
                || datasetId.startsWith("JNO-SW-3-")
                || datasetId.startsWith("JNO-J-3-"))
        {
            tmp = tokens[2]; 
        }
        
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
            String id = (name.startsWith("c/soho ")) ? "comet.soho" : targetMap.get(name);
            if(id == null)
            {
                System.out.println("WARNING: Unknown target name: " + name);
                continue;
            }
            
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
