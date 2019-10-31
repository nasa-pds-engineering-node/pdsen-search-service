package gov.nasa.pds.search.util;

import org.apache.http.client.config.RequestConfig;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClientBuilder;

public class HttpUtils
{
    public static CloseableHttpClient createHttpClient(int timeoutSec)
    {
        int timeoutMs = timeoutSec * 1000;
        
        RequestConfig.Builder configBuilder = RequestConfig.custom();
        configBuilder.setConnectTimeout(timeoutMs);
        configBuilder.setConnectionRequestTimeout(timeoutMs);
        configBuilder.setSocketTimeout(timeoutMs);        
        RequestConfig cfg = configBuilder.build();
        
        HttpClientBuilder clientBuilder = HttpClientBuilder.create();
        clientBuilder.setDefaultRequestConfig(cfg);        
        CloseableHttpClient client = clientBuilder.build();
        
        return client;
    }

}
