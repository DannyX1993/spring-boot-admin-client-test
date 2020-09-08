package com.dannyxnas.demo.config;

import org.apache.commons.io.output.ByteArrayOutputStream;

import javax.servlet.ServletOutputStream;
import javax.servlet.WriteListener;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpServletResponseWrapper;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;

public class ResponseWrapper extends HttpServletResponseWrapper {

    private HttpServletResponse response;
    //Output stream of cached response content
    private ByteArrayOutputStream result = new ByteArrayOutputStream();

    public ResponseWrapper(HttpServletResponse response) {
        super(response);
        this.response = response;
    }

    /**
     * Response content for external calls
     *For large response content, oom is easy to occur (for example, / Actor / logfile interface). api filtering can be performed where the method is called.
     *The solution is in step 4
     */
    public String body(){
        return result.toString();
    }

    @Override
    public ServletOutputStream getOutputStream() throws IOException {
        return new ServletOutputStreamWrapper(this.response,this.result);
    }

    @Override
    public PrintWriter getWriter() throws IOException {
        return new PrintWriter(new OutputStreamWriter(this.result,this.response.getCharacterEncoding()));
    }

    //Inner class of wrapper class for custom output stream
    static final class ServletOutputStreamWrapper extends ServletOutputStream{

        private HttpServletResponse response;
        private ByteArrayOutputStream byteArrayOutputStream;

        public ServletOutputStreamWrapper(HttpServletResponse response, ByteArrayOutputStream byteArrayOutputStream) {
            this.response = response;
            this.byteArrayOutputStream = byteArrayOutputStream;
        }

        @Override
        public boolean isReady() {
            return true;
        }

        @Override
        public void setWriteListener(WriteListener listener) {

        }

        @Override
        public void write(int b) throws IOException {
            this.byteArrayOutputStream.write(b);
        }

        /**
         * Refresh content back to the returned object and avoid multiple refreshes
         */
        @Override
        public void flush() throws IOException {
            if(!response.isCommitted()){
                byte[] bytes = this.byteArrayOutputStream.toByteArray();
                ServletOutputStream outputStream = response.getOutputStream();
                outputStream.write(bytes);
                outputStream.flush();
            }
        }
    }
}
