package com.dannyxnas.demo.config;

import org.springframework.boot.actuate.trace.http.HttpExchangeTracer;
import org.springframework.boot.actuate.trace.http.HttpTrace;
import org.springframework.boot.actuate.trace.http.HttpTraceRepository;
import org.springframework.core.Ordered;
import org.springframework.web.filter.OncePerRequestFilter;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;

public class CustomerHttpTraceFilter extends OncePerRequestFilter implements Ordered {
    //The repository that stores HttpTrace is memory by default, which can extend the way that this class stores data after swapping.
    private HttpTraceRepository httpTraceRepository;
    //This class creates HttpTrace objects. Set < include > contains the containers (request headers, response headers, remote address, time token) that we need to display those contents.
    private HttpExchangeTracer httpExchangeTracer;

    public CustomerHttpTraceFilter(HttpTraceRepository httpTraceRepository, HttpExchangeTracer httpExchangeTracer) {
        this.httpTraceRepository = httpTraceRepository;
        this.httpExchangeTracer = httpExchangeTracer;
    }

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException, IOException, ServletException {
//Verify that the URI is valid
        if (!isRequestValid(request)) {
            filterChain.doFilter(request, response);
            return;
        }
//Package HttpServletRequest as our own
        RequestWrapper wrapper = new RequestWrapper(request);
//Package HttpServletResponse as our own
        ResponseWrapper responseWrapper = new ResponseWrapper(response);

//Create our own TraceRequest object
        CustomerTraceableRequest traceableRequest = new CustomerTraceableRequest(wrapper);
//Create an HttpTrace object (filterdtraceablerequest is an internal class. Filter the information to be displayed and save it through set < include >), and focus on setting various parameters of the HttpTrace ා request object.
        HttpTrace httpTrace = httpExchangeTracer.receivedRequest(traceableRequest);
        try {
            filterChain.doFilter(wrapper, responseWrapper);
        } finally {
//Customized TraceableResponse saves the required response information
            CustomerTraceableResponse traceableResponse = new CustomerTraceableResponse(responseWrapper,request);
//Set the session, principal, timetoken information and Response internal class information in HttpTrace according to set < include >.
            this.httpExchangeTracer.sendingResponse(httpTrace, traceableResponse, null, null);
//Save the HttpTrace object in the repository
            this.httpTraceRepository.add(httpTrace);
        }
    }

    private boolean isRequestValid(HttpServletRequest request) {
        try {
            new URI(request.getRequestURL().toString());
            return true;
        } catch (URISyntaxException ex) {
            return false;
        }
    }

    @Override
    public int getOrder() {
        return Ordered.LOWEST_PRECEDENCE - 10;
    }
}
