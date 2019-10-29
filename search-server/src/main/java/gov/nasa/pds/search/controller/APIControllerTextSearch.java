package gov.nasa.pds.search.controller;

import java.util.List;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import gov.nasa.pds.nlp.NamedEntityRecognizer;
import gov.nasa.pds.nlp.Token;
import gov.nasa.pds.search.cfg.SearchServerConfiguration;

@RestController
@RequestMapping(path = "/api")
public class APIControllerTextSearch
{
    @Autowired
    private SearchServerConfiguration ssConfig;
    @Autowired
    private NamedEntityRecognizer ner;
    
    
    @GetMapping(path = "/search/text/v1")
    public void getSearch(HttpServletRequest httpReq, HttpServletResponse httpResp) throws Exception
    {
        String text = httpReq.getParameter("q");
        if(text == null) return;        
        
        List<Token> tokens = ner.parse(text);
        for(Token token: tokens)
        {
            System.out.println(token.text + " --> " + token.type);
        }
    }
}
