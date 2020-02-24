package tt;

import gov.nasa.pds.data.pds3.tools.SolrDumpProcessor_ResInvest;


public class ProcessResInvestDump
{

    public static void main(String[] args) throws Exception
    {
        SolrDumpProcessor_ResInvest processor = new SolrDumpProcessor_ResInvest();
        processor.processFile("/tmp/res_invest-0.xml", "/tmp/res-invest.xml");
    }

}
