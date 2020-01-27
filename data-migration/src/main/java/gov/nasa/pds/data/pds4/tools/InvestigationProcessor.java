package gov.nasa.pds.data.pds4.tools;

import java.util.Map;
import java.util.Set;

import gov.nasa.pds.data.pds4.model.Investigation;
import gov.nasa.pds.data.pds4.parser.ParserUtils;
import gov.nasa.pds.data.util.FieldMap;
import gov.nasa.pds.data.util.MapUtils;


public class InvestigationProcessor
{
    private Map<String, String> name2id;
    private Map<String, String> name2title;
    private Map<String, String> missingTargets;
    private Map<String, String> missingHosts;
    
    public InvestigationProcessor() throws Exception
    {
        name2id = MapUtils.loadMap("src/main/data/pds3/invest_name2id.txt");
        name2title = MapUtils.loadMap("src/main/data/pds3/invest_name2title.txt");
        missingTargets = MapUtils.loadMap("src/main/data/invest_missing_targets.txt");
        missingHosts = MapUtils.loadMap("src/main/data/invest_missing_hosts.txt");
    }

    
    public Investigation process(FieldMap fields)
    {
        Investigation data = new Investigation();
        
        data.lid = fields.getFirstValue("identifier");
        data.shortLid = ParserUtils.getShortLid(data.lid);
        data.vid = fields.getFirstValue("version_id");
        
        setInvestigationId(data, fields);
        setInvestigationTitle(data, fields);
        
        processInstrumentHosts(data, fields);
        processTargets(data, fields);
        
        return data;
    }
    
    
    private void setInvestigationId(Investigation data, FieldMap fields)
    {
        String name = fields.getFirstValue("investigation_name");
        String sid = name2id.get(name);

        if(sid == null)
        {
            String shortLid = ParserUtils.getShortLid(data.lid);
            sid = ParserUtils.getInvestigationId(shortLid);
            System.out.println("WARNING: Unknown name: " + name + ". Will use sid '" + sid + "'");
            data.id = new String[] { sid };
        }
        else
        {
            data.id = sid.split(",");
        }
    }


    private void setInvestigationTitle(Investigation data, FieldMap fields)
    {
        String name = fields.getFirstValue("investigation_name");
        String title = name2title.get(name);
        
        data.title = (title != null) ? title : fields.getFirstValue("title");
    }
    
    
    private void processInstrumentHosts(Investigation data, FieldMap fields)
    {
        Set<String> refs = fields.getValues("instrument_host_ref");
        if(refs == null)
        {
            String ids = missingHosts.get(data.shortLid);
            if(ids == null)
            {
                System.out.println("WARNING: No instrument_host_ref for " + data.shortLid);
                return;
            }
            
            data.hostIds = ids.split(",");
        }
        else
        {
            data.hostIds = new String[refs.size()];
            int i = 0;
            for(String ref: refs)
            {
                String shortLid = ParserUtils.getShortLid(ref);
                data.hostIds[i] = ParserUtils.getInstrumentHostId(shortLid);
                i++;
            }
        }
    }

    
    private void processTargets(Investigation data, FieldMap fields)
    {
        Set<String> refs = fields.getValues("target_ref");
        if(refs == null)
        {
            String shortRefs = missingTargets.get(data.shortLid);
            if(shortRefs == null)
            {
                System.out.println("WARNING: No target_ref for " + data.shortLid);
                return;
            }
            
            data.initTargets();
            String[] shortLids = shortRefs.split(",");
            for(String shortLid: shortLids)
            {
                addTarget(data, shortLid);
            }
        }
        else
        {
            data.initTargets();
            for(String ref: refs)
            {
                String shortLid = ParserUtils.getShortLid(ref);
                addTarget(data, shortLid);
            }
        }
    }

    
    private void addTarget(Investigation data, String shortLid)
    {
        String[] tuple = ParserUtils.getTargetTuple(shortLid);
        if(tuple == null || tuple.length != 2)
        {
            System.out.println("WARNING: Invalid target short lid: " + shortLid);
            return;
        }
        
        String tgtType = tuple[0];
        if(tgtType.equalsIgnoreCase("calibrator") || tgtType.equalsIgnoreCase("calibration_field")) return;
        
        String tgtName = tuple[1];
        
        int idx = tgtName.indexOf('.');
        if(idx > 0) tgtName = tgtName.substring(idx+1);

        data.targetIds.add(tgtName);
        data.targetTypes.add(tgtType);
    }
}
