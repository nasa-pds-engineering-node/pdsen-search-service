package tt;

import gov.nasa.pds.data.solr.LegacyExporter;

public class TestExportDataFromSolr
{

    public static void main(String[] args) throws Exception
    {
        String solrUrl = "https://pds.nasa.gov/services/search";
        String requestHandler = "/pds/archive-filter";
        //String strQuery = "objectType:Product_Data_Set_PDS3 AND instrument_id:LROC";
        String strQuery = "data_class:Resource";
        String primaryKey = "search_id";
        String exportPath = "/tmp/resources.xml";

        LegacyExporter exporter = new LegacyExporter(solrUrl, requestHandler);
        exporter.setQuery(strQuery, primaryKey);
        exporter.export(exportPath);
        exporter.close();
    }

}
