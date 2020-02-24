package tt;

import java.util.Arrays;
import java.util.List;

import gov.nasa.pds.solr.query.LuceneQueryBuilder;


public class TestQueryBuilder
{

    public static void main(String[] args)
    {
        LuceneQueryBuilder bld = new LuceneQueryBuilder();
        bld.addField(true, "investigation_id", "bennu");
        
        List<String> list = Arrays.asList(new String[] {"images"});
        
        bld.addGroupStart(true);
        bld.addField(false, "search_p1", list);
        bld.addBoost(10);
        bld.addField(false, "description", list);
        bld.addGroupEnd();
 
        bld.addField(true, "science_facets", "Solar Energetic");
        
        // +investigation_id:orex +(title:(camera)^10 description:(camera))
        System.out.println(bld);
    }

}
