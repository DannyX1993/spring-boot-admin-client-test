package com.dannyxnas.demo.config;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import org.apache.commons.io.IOUtils;

import javax.servlet.ReadListener;
import javax.servlet.ServletInputStream;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletRequestWrapper;
import java.io.*;

public class RequestWrapper extends HttpServletRequestWrapper {
    //Store the message body of the request (CACHE one copy first)
    private byte[] body;
    //Customize the wrapper class of the input stream to write the cached data to the stream again
    private ServletInputStreamWrapper wrapper;
    private final Logger logger = LoggerFactory.getLogger(RequestWrapper.class);

    public RequestWrapper(HttpServletRequest request) {
        super(request);
        try {
//Using Apache's commons IO tool to read data from request first
            body = IOUtils.toByteArray(request.getInputStream());
        } catch (IOException e) {
            logger.error("Exception getting request parameters from request:", e);
        }
//Write the read memory back to the stream
        wrapper = new ServletInputStreamWrapper(new ByteArrayInputStream(body));
    }
    //Convert to String for external calls and replace escape characters
    public String body() {
        return new String(body).replaceAll("[\n\t\r]","");
    }
    //Return our custom stream wrapper class for system call to read data
    @Override
    public ServletInputStream getInputStream() throws IOException {
        return this.wrapper;
    }
    //Return our custom stream wrapper class for system call to read data
    @Override
    public BufferedReader getReader() throws IOException {
        return new BufferedReader(new InputStreamReader(this.wrapper));
    }
    //Read data from a given input stream
    static final class ServletInputStreamWrapper extends ServletInputStream {

        private InputStream inputStream;

        public ServletInputStreamWrapper(ByteArrayInputStream inputStream) {
            this.inputStream = inputStream;
        }

        @Override
        public boolean isFinished() {
            return true;
        }

        @Override
        public boolean isReady() {
            return false;
        }

        @Override
        public void setReadListener(ReadListener listener) {

        }
        //Read cache data
        @Override
        public int read() throws IOException {
            return this.inputStream.read();
        }

        public InputStream getInputStream() {
            return inputStream;
        }

        public void setInputStream(InputStream inputStream) {
            this.inputStream = inputStream;
        }
    }
}
