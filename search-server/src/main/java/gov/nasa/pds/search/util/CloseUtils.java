package gov.nasa.pds.search.util;

import java.io.Closeable;

public class CloseUtils
{
    public static void safeClose(Closeable cl)
    {
        if(cl == null) return;
        
        try
        {
            cl.close();
        }
        catch(Exception ex)
        {
            // Ignore
        }
    }
}
