package gov.nasa.pds.search.solr;

import java.io.IOException;
import java.util.List;

import org.apache.solr.common.SolrDocumentList;

import gov.nasa.pds.search.util.NameMapper;

public interface IResponseWriter
{
    public void setFields(List<String> fields);
    public void setNameMapper(NameMapper nameMapper);
    
    public void error(String msg) throws IOException;
    public void write(SolrDocumentList docList) throws IOException;
}
