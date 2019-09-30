package gov.nasa.pds.search.controller;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;


@RestController
@RequestMapping(path = "/api")
public class APIController 
{
	@GetMapping(path="/search", produces = "application/json")
    public String getEmployees()
    {
        return "{}";
    }
}
