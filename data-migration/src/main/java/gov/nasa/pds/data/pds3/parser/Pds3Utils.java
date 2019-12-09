package gov.nasa.pds.data.pds3.parser;

public class Pds3Utils
{
    public static String getShortLid(String lid)
    {
        int idx;

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

}
