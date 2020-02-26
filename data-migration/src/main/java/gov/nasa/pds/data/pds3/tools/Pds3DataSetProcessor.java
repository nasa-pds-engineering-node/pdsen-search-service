package gov.nasa.pds.data.pds3.tools;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import java.util.TreeSet;

import gov.nasa.pds.data.pds3.model.Pds3DataCollection;
import gov.nasa.pds.data.pds4.parser.ParserUtils;
import gov.nasa.pds.data.util.FieldMap;
import gov.nasa.pds.data.util.MapUtils;


public class Pds3DataSetProcessor
{
    private Map<String, String> investMap = new HashMap<>(100);
    private Map<String, String> targetMap = new HashMap<>(1000);
    private Map<String, String> instrMap = new HashMap<>(100);
    
    private Pds3DataClassifier scienceFacetsClassifier;
    private Pds3DataClassifier keywordsClassifier;    
    
    
    public Pds3DataSetProcessor() throws Exception
    {
        MapUtils.loadMap(investMap, "src/main/data/pds3/invest_name2id.txt");
        MapUtils.loadMap(targetMap, "src/main/data/pds3/target_name2id.txt");
        MapUtils.loadMap(instrMap, "src/main/data/pds3/instr_id2type.txt");
        scienceFacetsClassifier = new Pds3DataClassifier("src/main/data/pds3/classifier/science_facets.dic");
        keywordsClassifier = new Pds3DataClassifier("src/main/data/pds3/classifier/keywords.dic");
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
        if(data.instrumentTypes == null) return;

        for(String itype: data.instrumentTypes)
        {
            boolean found = scienceFacetsClassifier.extractKeywords(data.scienceFacets, itype);
            if(!found) 
            {
                System.out.println("WARNING: Could not classify instrument type " + itype + " (" + data.lid + ")"); 
            }
        }
    }
    

    private void classifyDataByDescription(Pds3DataCollection data, FieldMap fields)
    {
        if(data.description == null) return;
        
        // Science facets
        String text = fields.getFirstValue("data_set_terse_description");
        scienceFacetsClassifier.extractKeywords(data.scienceFacets, text);

        text = fields.getFirstValue("description");
        scienceFacetsClassifier.extractKeywords(data.scienceFacets, text);

        text = fields.getFirstValue("abstract_text");
        scienceFacetsClassifier.extractKeywords(data.scienceFacets, text);
        
        text = fields.getFirstValue("data_set_description");
        if(text != null && text.length() > 500) text = text.substring(0, 500);
        scienceFacetsClassifier.extractKeywords(data.scienceFacets, text);        
        
        // Generic Keywords
        text = fields.getFirstValue("data_set_terse_description");
        keywordsClassifier.extractKeywords(data.keywords, text);
        
        text = fields.getFirstValue("description");
        keywordsClassifier.extractKeywords(data.keywords, text);
        
        text = fields.getFirstValue("abstract_text");
        keywordsClassifier.extractKeywords(data.keywords, text);
        
        text = fields.getFirstValue("data_set_description");
        keywordsClassifier.extractKeywords(data.keywords, text);
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
            data.description.add(descr);
        }
    }
    
    
    private void processInstrumentHost(Pds3DataCollection data, FieldMap fields)
    {
        Set<String> ihIds = fields.getValues("instrument_host_id");
        if(ihIds == null)
        {
            String id;
            
            if(data.investigationIds.contains("bepicolombo"))
            {
                id = "BC";
            }
            else if(data.investigationIds.contains("exomars"))
            {
                id = "EM16";
            }
            else
            {
                String[] tokens = data.datasetId.split("-");
                id = tokens[0];
            }
            
            if(id.equals("MEX") || id.equals("VEX")
                    || id.equals("GIO") || id.equals("HP") 
                    || id.equals("HST") || id.equals("VG2") 
                    || id.equals("RO") || id.equals("RL")
                    || id.equals("S1") || id.equals("BC") || id.equals("EM16"))
            {
                ihIds = new TreeSet<>();
                ihIds.add(id);
            }
            else if(id.equals("RO/RL"))
            {
                ihIds = new TreeSet<>();
                ihIds.add("RO");
                ihIds.add("RL");
            }
            else
            {
                System.out.println("WARNING: No instrument host id for " + data.lid);
                return;
            }
        }

        /*
        if(ihIds.size() > 1)
        {
            System.out.println("WARNING: Multiple instrument host ids for " + data.lid);
        }
        */
        
        data.instrumentHostIds = ihIds;
    }


    // !!! Call this method after setting instrument host ids !!!
    private void processInstruments(Pds3DataCollection data, FieldMap fields)
    {
        // Instrument IDs
        data.instrumentIds = fields.getValues("instrument_id");
        if(data.instrumentIds == null)
        {
            data.instrumentIds = fields.getValues("instrument_name");
            if(data.instrumentIds == null)
            {
                System.out.println("WARNING: Missing instrument id/name (" + data.lid + ")");
                return;
            }
        }
        
        // Instrument Types
        // Lookup in dictionary first
        data.instrumentTypes = getInstrumentTypes(data.instrumentHostIds, data.instrumentIds);
        
        // Not in dictionary. Get from data set.
        if(data.instrumentTypes == null)
        {
            data.instrumentTypes = fields.getValues("instrument_type");
        }
        
        // Missing instrument types.
        if(data.instrumentTypes == null && !data.instrumentIds.contains("SPICE"))
        {
            System.out.println("WARNING: Missing instrument type (" + data.lid + ")");
        }
    }

    
    private Set<String> getInstrumentTypes(Set<String> hostIds, Set<String> instrIds)
    {
        if(hostIds == null || instrIds == null) return null;
        
        Set<String> set = new TreeSet<>();
        
        for(String hostId: hostIds)
        {
            for(String instrId: instrIds)
            {
                String key = instrId + "." + hostId;
                key = key.toLowerCase();
                String types = instrMap.get(key);
                if(types != null)
                {
                    String[] tokens = types.split(",");
                    Collections.addAll(set, tokens);
                }
            }
        }
        
        return set.size() == 0 ? null : set;
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
        data.processingLevels = new TreeSet<>();
        data.codmacLevels = new TreeSet<>();
        
        String[] tokens = (data.datasetId.indexOf('-') > 0) ? data.datasetId.split("-") : data.datasetId.split("_");
        if(tokens.length < 4)
        {
            System.out.println("WARNING: Could not extract CODMAC level from data_set_id " + data.datasetId);
            return;
        }
        
        // CODMAC levels separated by /
        String tmp = tokens[3];
        // Fix invalid data
        if(data.datasetId.startsWith("CO-SR-UVIS-HSP") 
                || data.datasetId.startsWith("MGS-M-MOC-NA/WA")
                || data.datasetId.startsWith("MSL-M-CHEMCAM-")
                || data.datasetId.startsWith("CH1-ORB-L-M3-")
                || data.datasetId.startsWith("LCROSS-E/L-NSP1-FL-")
                || data.datasetId.startsWith("LCROSS-X-NSP2-FL-")
                || data.datasetId.startsWith("CH1-ORB-L-MRFFR-")
                || data.datasetId.startsWith("ULY-J-COSPIN-"))
        {
            tmp = tokens[4];
        }
        else if(data.datasetId.startsWith("NEAR-A-5-") 
                || data.datasetId.startsWith("NEAR-MSI-6-")
                || data.datasetId.startsWith("LP-L-6-")
                || data.datasetId.startsWith("JNO-SS-3-")
                || data.datasetId.startsWith("JNO-SW-3-")
                || data.datasetId.startsWith("JNO-J-3-"))
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
                        + " (data_set_id: " + data.datasetId + ")");
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
        String[] tokens = data.datasetId.split("-");
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
