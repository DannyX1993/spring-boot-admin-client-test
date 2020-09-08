package com.dannyxnas.demo.config;

import com.jeesuite.springweb.utils.IpUtils;
import org.springframework.boot.actuate.trace.http.TraceableRequest;
import org.springframework.http.HttpMethod;

import java.net.URI;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class CustomerTraceableRequest implements TraceableRequest {
    //Custom Request decoration class, cannot use HttpServletRequest
    private RequestWrapper request;

    public CustomerTraceableRequest(RequestWrapper request) {
        this.request = request;
    }
    //getMethod in HttpTrace class will call
    @Override
    public String getMethod() {
        return request.getMethod();
    }

    /**
     * @return POST Or GET returns {ip}:{port}/uir
     */
    @Override
    public URI getUri() {
        return URI.create(request.getRequestURL().toString());
    }

    //Because the only extensible Map in HttpTrace is the header Map, our custom attribute RequestParam is stored in the headers as the input information.
    @Override
    public Map<String, List<String>> getHeaders() {
        Map<String, List<String>> headerParam = new HashMap<>(1);
        headerParam.put("RequestParam",getParams());
        return headerParam;
    }

    //This method also needs to be rewritten. The default is too simple to get the real IP address.
    @Override
    public String getRemoteAddress() {
        return IpUtils.getIpAddr(request);
    }
    //According to the different request methods of GET or POST, GET the request parameters in different situations
    public List<String> getParams() {
        String params = null;
        String method = this.getMethod();
        if(HttpMethod.GET.matches(method)){
            params = request.getQueryString();
        }else if(HttpMethod.POST.matches(method)){
            params = this.request.body();
        }
        List<String> result = new ArrayList<>(1);
        result.add(params);
        return result;
    }
}
