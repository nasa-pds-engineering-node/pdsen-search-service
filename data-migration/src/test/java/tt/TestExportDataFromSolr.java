package tt;

import gov.nasa.pds.data.solr.LegacyExporter;

public class TestExportDataFromSolr
{

    public static void main(String[] args) throws Exception
    {
        String solrUrl = "https://pds.nasa.gov/services/search";
        String requestHandler = "/pds/archive-filter";
        //String strQuery = "objectType:Product_Data_Set_PDS3 AND instrument_id:LROC";
        
        // Resources
        //String strQuery = "data_class:Resource";
        String strQuery = "resource_type:Information.Investigation";
        
        // Instruments
        //String strQuery = "objectType:Product_Instrument_PDS3";   // PDS3
        //String strQuery = "data_class:Instrument";                  // PDS4

        // Instrument Host
        //String strQuery = "objectType:Product_Instrument_Host_PDS3";   // PDS3
        //String strQuery = "data_class:Instrument_Host";                  // PDS4
        
        // Missions
        //String strQuery = "objectType:Product_Mission_PDS3";   // PDS3
        //String strQuery = "data_class:Investigation";            // PDS4

        
        String primaryKey = "search_id";
        String exportPath = "/tmp/res_invest.xml";

        LegacyExporter exporter = new LegacyExporter(solrUrl, requestHandler);
        exporter.setQuery(strQuery, primaryKey);
        exporter.export(exportPath);
        exporter.close();
    }

}
