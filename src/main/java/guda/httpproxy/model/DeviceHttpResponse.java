package guda.httpproxy.model;

import guda.httpproxy.util.IO;
import guda.httpproxy.watch.ProxyDispatch;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.util.StringUtils;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by well on 2014/12/6.
 */
public class DeviceHttpResponse {

    public static final Logger log = LoggerFactory.getLogger(DeviceHttpResponse.class);

    public static final String RESPONSE_HEADER_Date = "Date";
    public static final String RESPONSE_HEADER_Server = "Server";
    public static final String RESPONSE_HEADER_Connection = "Connection";
    public static final String RESPONSE_HEADER_Content_Type = "Content-Type";
    public static final String RESPONSE_HEADER_Transfer_Encoding = "Transfer-Encoding";
    public static final String RESPONSE_HEADER_TE = "TE";
    public static final String RESPONSE_Content_Encoding = "Content-Encoding";

    private Map<String,String> header = new HashMap<String, String>();

    private StringBuilder headerBuff = new StringBuilder();

    private String body;

    private String status;

    private String httpVersion;

    private byte[] responseStream;

    private String charset  = "UTF-8";

    private String time = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss:ssss").format(new Date());


    public DeviceHttpResponse(byte[] response){
        responseStream = response;
        if(responseStream == null){
            return ;
        }
        /**
         *   HTTP/1.1 200 OK
             Date: Tue, 14 Sep 1999 02:19:57 GMT
             Server: Apache/1.2.6
             Connection: close
             Content-Type: text/html
             Content-Type:text/html;charset=GB2312
         **空行**
         */
        ByteArrayInputStream byteArrayInputStream = new ByteArrayInputStream(response);
        try {
            String firstLine = IO.readStringLine(byteArrayInputStream,charset);
            headerBuff.append(firstLine);
            String[] split = firstLine.split(" ");
            httpVersion = split[0];
            if(split.length>1) {
                status = split[1];
            }
            String line = IO.readStringLine(byteArrayInputStream,charset);
            while(StringUtils.hasText(line)){
                headerBuff.append(line);
                String[] split1 = line.split(": ");
                if(split1.length ==2){
                    header.put(split1[0],split1[1]);
                }
                line = IO.readStringLine(byteArrayInputStream,charset);
            }
            String contentType = header.get(DeviceHttpResponse.RESPONSE_HEADER_Content_Type);
            if(contentType == null){
                headerBuff.append(ProxyDispatch.CRLF).append("response head is stream... can not parse,ignore ....");
                return ;
            }
            String[] split1 = contentType.split(";");
            if(!split1[0].startsWith("text")&&!split1[0].startsWith("application/json") &&!split1[0].startsWith("application/xml")&&!split1[0].startsWith("application/text") ){
                body = "response body is stream can not parse,ignore ....";
                return;
            }
            if(split1.length>1){
                String[] splitCharset = split1[1].split("=");
                if(splitCharset.length ==2){
                    charset = splitCharset[1].toUpperCase().trim();
                }
            }
            byte[] bytes = IO.toByteArray(byteArrayInputStream);
            if(isChunked()){

                byte[] bytes1 = IO.readChunked(bytes);
                if(isGZipContent()){
                    body =  IO.uncompress(bytes1, 0, bytes1.length, charset);
                }else{
                    body = new String(bytes1,charset);
                }
            }else {
                if(isGZipContent()){
                    body =  IO.uncompress(bytes, 0, bytes.length, charset);
                }else{
                    body = new String(bytes,charset);
                }
            }

        }catch(Exception e){
            log.error("",e);
        }finally{
            if(byteArrayInputStream!=null){
                try {
                    byteArrayInputStream.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    public boolean isGZipContent() {
        final String encoding = header.get(DeviceHttpResponse.RESPONSE_Content_Encoding);
        if (null != encoding) {
            if (encoding.toLowerCase().indexOf("gzip") > -1) {
                return true;
            }
        }
        return false;
    }

    public boolean isChunked(){
        String s = header.get(DeviceHttpResponse.RESPONSE_HEADER_Transfer_Encoding);
        if (s!=null && s.indexOf("chunked") >= 0){
            return true;
        }
        s = header.get(DeviceHttpResponse.RESPONSE_HEADER_TE);
        if (s!=null && s.indexOf("chunked") >= 0){
            return true;
        }
        return false;
    }

    public String getCharset() {
        return charset;
    }

    public void setCharset(String charset) {
        this.charset = charset;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getHttpVersion() {
        return httpVersion;
    }

    public void setHttpVersion(String httpVersion) {
        this.httpVersion = httpVersion;
    }

    public byte[] getResponseStream() {
        return responseStream;
    }

    public void setResponseStream(byte[] responseStream) {
        this.responseStream = responseStream;
    }

    public Map<String, String> getHeader() {
        return header;
    }

    public void setHeader(Map<String, String> header) {
        this.header = header;
    }

    public String getBody() {
        return body;
    }

    public void setBody(String body) {
        this.body = body;
    }

    public String toString(){
        StringBuilder buf = new StringBuilder();
        buf.append("Response Header:").append("[time:]").append(time).append(ProxyDispatch.CRLF);
        buf.append(headerBuff);
        buf.append(ProxyDispatch.CRLF);
        buf.append("Response Body:").append(ProxyDispatch.CRLF).append(body);
        return buf.toString();
    }
}
