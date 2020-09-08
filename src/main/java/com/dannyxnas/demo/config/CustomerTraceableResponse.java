package com.dannyxnas.demo.config;

import org.springframework.boot.actuate.trace.http.TraceableResponse;
import org.springframework.util.AntPathMatcher;

import javax.servlet.http.HttpServletRequest;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

public class CustomerTraceableResponse implements TraceableResponse {
    //Custom HttpServletResponse wrapper class
    private ResponseWrapper response;
    private HttpServletRequest request;

    public CustomerTraceableResponse(ResponseWrapper response, HttpServletRequest request) {
        this.response = response;
        this.request = request;
    }
    //Return to response status
    @Override
    public int getStatus() {
        return response.getStatus();
    }
    //Expand the Response headers to add the Response Body attribute to display the response content, but you need to exclude the requests beginning with '/ Actor /', some of which are too large and easy to OOM.
    @Override
    public Map<String, List<String>> getHeaders() {
        if(isActuatorUri()){
            return extractHeaders();
        }else{
            Map<String, List<String>> result = new LinkedHashMap<>(1);
            List<String> responseBody = new ArrayList<>(1);
            responseBody.add(this.response.body());
            result.put("ResponseBody", responseBody);
            result.put("Content-Type", getContentType());
            return result;
        }
    }
    //Whether it is the request uri to be filtered
    private boolean isActuatorUri() {
        String requestUri = request.getRequestURI();
        AntPathMatcher matcher = new AntPathMatcher();
        return matcher.match("/actuator/**", requestUri);
    }
    //The content type and Length displayed on the server-side page are obtained from the Response
    private List<String> getContentType() {
        List<String> list = new ArrayList<>(1);
        list.add(this.response.getContentType());
        return list;
    }
    //For the request of / Actor / *, return the default headers content.
    private Map<String, List<String>> extractHeaders() {
        Map<String, List<String>> headers = new LinkedHashMap<>();
        for (String name : this.response.getHeaderNames()) {
            headers.put(name, new ArrayList<>(this.response.getHeaders(name)));
        }
        return headers;
    }
}
