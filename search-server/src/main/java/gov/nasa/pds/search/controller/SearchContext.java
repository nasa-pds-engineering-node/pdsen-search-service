package gov.nasa.pds.search.controller;

import java.io.IOException;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.solr.common.SolrDocumentList;

import gov.nasa.pds.search.solr.SolrDocJsonWriter;
import gov.nasa.pds.search.util.RequestParameters;

public class SearchContext
{
    public RequestParameters reqParams;
    public HttpServletResponse httpResp;
    public SolrDocJsonWriter respWriter;
    public String qParam;
    
    
    public SearchContext(HttpServletRequest httpReq, HttpServletResponse httpResp) throws IOException
    {
        this.reqParams = new RequestParameters(httpReq.getParameterMap());
        
        this.httpResp = httpResp;
        this.httpResp.setContentType("application/json");

        this.respWriter = new SolrDocJsonWriter(httpResp.getWriter());
    
        this.qParam = reqParams.getParameter("q");
    }
    
    
    public boolean validateAndContinue() throws IOException
    {
        if(qParam == null || qParam.isEmpty())
        {
            httpResp.setStatus(400);
            respWriter.error("Missing query parameter (q=...)");
            return false;
        }
        
        return true;
    }

    
    public boolean validateAndContinue(SolrDocumentList solrDocs) throws IOException
    {
        if(solrDocs == null)
        {
            httpResp.setStatus(400);
            respWriter.error("Invalid query");
            return false;
        }
        
        return true;
    }
}
