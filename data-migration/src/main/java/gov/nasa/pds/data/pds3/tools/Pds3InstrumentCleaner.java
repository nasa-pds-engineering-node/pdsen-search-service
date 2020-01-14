package gov.nasa.pds.data.pds3.tools;

import java.io.FileWriter;
import java.io.Writer;
import java.util.Map;

import org.apache.commons.lang.StringUtils;
import org.apache.commons.lang.WordUtils;

import gov.nasa.pds.data.pds3.model.Pds3Instrument;
import gov.nasa.pds.data.pds3.parser.Pds3Utils;
import gov.nasa.pds.data.util.xml.SolrDocParser;
import gov.nasa.pds.data.util.xml.SolrDocUtils;


public class Pds3InstrumentCleaner
{
    private static class CleanCB implements SolrDocParser.Callback
    {
        private Pds3Instrument instrument;
        private Writer writer;

        private Map<String, String> host2mission;
        
        
        public CleanCB(Map<String, String> missionMap, Writer writer)
        {
            this.host2mission = missionMap;
            this.writer = writer;
        }
        
        @Override
        public void onDocStart()
        {
            instrument = new Pds3Instrument();
        }


        @Override
        public void onField(String name, String value)
        {
            if(name.equals("identifier"))
            {
                instrument.shortLid = Pds3Utils.getShortLid(value);
                instrument.id = Pds3Utils.extractInstrumentId(instrument.shortLid);
                return;
            }

            if(name.equals("instrument_name"))
            {
                value = StringUtils.normalizeSpace(value);
                value = WordUtils.capitalizeFully(value);
                instrument.name = value;
                return;
            }
            
            if(name.equals("instrument_host_id"))
            {
                instrument.instrumentHostId = value.toLowerCase();
                return;
            }
            
            if(name.equals("instrument_type"))
            {
                value = StringUtils.normalizeSpace(value);
                if(value.equalsIgnoreCase("n/a") || value.equalsIgnoreCase("Unknown")) return;
                
                value = WordUtils.capitalizeFully(value);
                instrument.instrumentTypes.add(value);
                return;
            }
            
        }
        

        @Override
        public boolean onDocEnd()
        {
            try
            {
                if(instrument.instrumentHostId == null) return true;
                if(instrument.instrumentTypes.isEmpty())
                {
                    return true;
                }

                String investigationId = host2mission.get(instrument.instrumentHostId);
                if(investigationId == null)
                {
                    //System.out.println("ERROR: Investigation is unknown for instrument host " + instrument.instrumentHostId);
                    investigationId = instrument.instrumentHostId;
                }
                
                if(!instrument.instrumentHostId.equals("vex")) return true;
                
                writer.append("<doc>\n");

                String sid = instrument.id + "." + instrument.instrumentHostId;
                
                SolrDocUtils.writeField(writer, "sid", sid);
                SolrDocUtils.writeField(writer, "instrument_id", instrument.id);
                SolrDocUtils.writeField(writer, "instrument_name", instrument.name);
                SolrDocUtils.writeField(writer, "instrument_type", instrument.instrumentTypes.toArray(new String[0]));
                SolrDocUtils.writeField(writer, "instrument_host_id", instrument.instrumentHostId);
                SolrDocUtils.writeField(writer, "investigation_id", investigationId.toLowerCase());
                
                writer.append("</doc>\n");
            }
            catch(Exception ex)
            {
                ex.printStackTrace();
                return false;
            }
            
            return true;
        }
    }
    
    ////////////////////////////////////////////////////////////////////////////////////////    
    
    public static void processFile(String inPath, String outPath, Map<String, String> missionMap) throws Exception
    {
        Writer writer = new FileWriter(outPath);
        CleanCB cb = new CleanCB(missionMap, writer);
        SolrDocParser parser = new SolrDocParser(inPath, cb);
        
        writer.write("<add>\n");
        parser.parse();
        writer.write("</add>\n");
        
        writer.close();
        parser.close();
    }

}
