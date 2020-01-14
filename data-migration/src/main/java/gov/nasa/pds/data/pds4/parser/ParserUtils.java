package gov.nasa.pds.data.pds4.parser;

public class ParserUtils
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

    
    public static String getInstrumentId(String shortLid)
    {
        if(shortLid == null) return null;
        if(shortLid.startsWith("instrument.")) shortLid = shortLid.substring(11);
        
        int idx = shortLid.indexOf(".");
        if(idx > 0) return shortLid.substring(0, idx);
        
        idx = shortLid.indexOf("__");
        if(idx > 0) return shortLid.substring(0, idx);
        
        return null;
    }


    public static String getInvestigationId(String shortLid)
    {
        if(shortLid == null) return null;
        
        int idx = shortLid.indexOf('.');
        if(idx > 0)
        {
            return shortLid.substring(idx+1);
        }
        
        return null;
    }

    
    public static String getInstrumentHostId(String shortLid)
    {
        if(shortLid == null) return null;
        
        int idx = shortLid.indexOf('.');
        if(idx > 0)
        {
            return shortLid.substring(idx+1);
        }
        
        return null;
    }


    public static String[] getTargetTuple(String shortLid)
    {
        if(shortLid == null) return null;
        
        int idx = shortLid.indexOf('.');
        if(idx > 0)
        {
            return new String[] { shortLid.substring(0, idx), shortLid.substring(idx+1) };
        }
        
        return null;
    }
    
}
