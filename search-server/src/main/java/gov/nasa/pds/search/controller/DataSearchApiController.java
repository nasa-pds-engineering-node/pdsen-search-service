package gov.nasa.pds.search.controller;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.json.JSONObject;
import org.json.JSONTokener;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import gov.nasa.pds.search.solr.QueryResponseJsonWriter;


@RestController
@RequestMapping(path = "/api/v2")
public class DataSearchApiController
{
    
    @PostMapping(path = "/search/data", consumes = "application/json")
    public void getData(HttpServletRequest httpReq, HttpServletResponse httpResp) throws Exception
    {
        httpResp.setContentType("application/json");
        QueryResponseJsonWriter respWriter = new QueryResponseJsonWriter(httpResp.getWriter());
        
        try
        {
            JSONTokener tok = new JSONTokener(httpReq.getReader());
            JSONObject json = new JSONObject(tok);
        }
        catch(Exception ex)
        {
            respWriter.error(400, ex.getMessage());
        }
    }
    
}
