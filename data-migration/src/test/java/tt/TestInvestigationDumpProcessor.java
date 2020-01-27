package tt;

import gov.nasa.pds.data.pds4.tools.SolrDumpProcessor_Investigation;

public class TestInvestigationDumpProcessor
{

    public static void main(String[] args) throws Exception
    {
        SolrDumpProcessor_Investigation processor = new SolrDumpProcessor_Investigation();
        processor.processFile("/ws/data/prod/missions_pds4-0.xml", "/tmp/missions_pds4-new.xml");
    }

}
