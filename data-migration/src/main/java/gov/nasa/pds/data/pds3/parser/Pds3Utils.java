package gov.nasa.pds.data.pds3.parser;

public class Pds3Utils
{
    public static String getShortLid(String lid)
    {
        if(lid == null) return null;
        
        // Remove version
        int idx = lid.indexOf("::");
        if(idx > 0) lid = lid.substring(0, idx);

        idx = lid.indexOf(":target:");
        if(idx > 0) return lid.substring(idx + 8);

        idx = lid.indexOf(":instrument:");
        if(idx > 0) return lid.substring(idx + 12);

        idx = lid.indexOf(":instrument_host:");
        if(idx > 0) return lid.substring(idx + 17);
        
        idx = lid.indexOf(":investigation:");
        if(idx > 0) return lid.substring(idx + 15);
        
        return null;
    }
    
    
    public static void toShortLid(String[] refs)
    {
        if(refs == null) return;
        for(int i = 0; i < refs.length; i++)
        {
            String ref = refs[i];
            
            // Remove version
            int idx = ref.indexOf("::");
            if(idx > 0) ref = ref.substring(0, idx);
            
            // Convert to short lid
            refs[i] = getShortLid(ref);
        }        
    }

    
    public static String extractInstrumentId(String shortLid)
    {
        if(shortLid == null) return null;
        if(shortLid.startsWith("instrument.")) shortLid = shortLid.substring(11);
        
        if(shortLid.startsWith("dawn."))
        {
            return shortLid.substring(5);
        }

        if(shortLid.startsWith("vex."))
        {
            return shortLid.substring(4);
        }

        int idx = shortLid.indexOf(".");
        if(idx > 0) return shortLid.substring(0, idx);
        
        idx = shortLid.indexOf("__");
        if(idx > 0) return shortLid.substring(0, idx);
        
        return null;
    }

}
