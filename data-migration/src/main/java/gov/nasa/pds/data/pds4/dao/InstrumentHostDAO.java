package gov.nasa.pds.data.pds4.dao;

import gov.nasa.pds.data.pds4.model.InstrumentHost;

public interface InstrumentHostDAO
{
    public float getVersion(String lid) throws Exception;
    public void save(InstrumentHost ih) throws Exception;
    public void update(InstrumentHost ih) throws Exception;
}
