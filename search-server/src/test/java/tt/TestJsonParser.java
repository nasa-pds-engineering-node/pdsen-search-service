package tt;

import java.io.FileReader;

import org.json.JSONObject;
import org.json.JSONTokener;

public class TestJsonParser
{

    public static void main(String[] args) throws Exception
    {
        JSONTokener tok = new JSONTokener(new FileReader("src/test/data/request1.json"));
        JSONObject json = new JSONObject(tok);
        
        JSONObject jFilters = json.getJSONObject("filters");
        String[] filterNames = JSONObject.getNames(jFilters);
        
        for(String filterName: filterNames)
        {
            System.out.println(filterName);
        }
    }

}
