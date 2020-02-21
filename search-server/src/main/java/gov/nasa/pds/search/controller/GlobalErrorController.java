package gov.nasa.pds.search.controller;

import java.io.IOException;

import javax.servlet.RequestDispatcher;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.boot.web.servlet.error.ErrorController;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.DispatcherServlet;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.ObjectMapper;


@Controller
public class GlobalErrorController implements ErrorController  
{
    
    @RequestMapping("/error")
    public void handleError(HttpServletRequest httpReq, HttpServletResponse httpResp) throws IOException
    {
        String message = getErrorMessage(httpReq);
        
        ObjectMapper mapper = new ObjectMapper();
        JsonGenerator jgen = mapper.getFactory().createGenerator(httpResp.getWriter());
        
        httpResp.setContentType("application/json");
        
        jgen.writeStartObject(); // Root
        
        jgen.writeFieldName("response");

        jgen.writeStartObject();
        jgen.writeStringField("status", "error");
        jgen.writeNumberField("status_code", httpResp.getStatus());
        jgen.writeStringField("error_message", message);
        jgen.writeEndObject();
        
        jgen.writeEndObject(); // Root
        
        jgen.close();
    }
 
    
    @Override
    public String getErrorPath() 
    {
        return "/error";
    }
    
    
    private String getErrorMessage(HttpServletRequest httpReq)
    {
        String statusCode = String.valueOf(httpReq.getAttribute(RequestDispatcher.ERROR_STATUS_CODE));
        String message = "";
        
        Object obj = httpReq.getAttribute(RequestDispatcher.ERROR_MESSAGE);
        if(obj != null) 
        {
            message = obj.toString();
        }
        
        if(message == null || message.isEmpty())
        {
            obj = httpReq.getAttribute(DispatcherServlet.EXCEPTION_ATTRIBUTE);   
            if(obj != null && obj instanceof Exception)
            {
                Exception ex = (Exception)obj;
                message = ex.getMessage();
            }
        }
        
        if(message == null || message.isEmpty())
        {
            if("404".equals(statusCode)) return "Invalid URL";
        }
        
        return message;
    }
}
