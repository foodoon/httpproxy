package guda.httpproxy.model;

import guda.httpproxy.util.IO;
import guda.httpproxy.watch.ProxyDispatch;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.util.StringUtils;

import java.io.ByteArrayInputStream;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by well on 2014/12/6.
 */
public class DeviceHttpRequest {

    public static final Logger log = LoggerFactory.getLogger(DeviceHttpRequest.class);

   public static final String REQUEST_HEADER_User_Agent = "User-Agent";
    public static final String REQUEST_HEADER_Host = "Host";
    public static final String REQUEST_HEADER_Accept = "Accept";
    public static final String REQUEST_HEADER_Accept_Language = "Accept-Language";
    public static final String REQUEST_HEADER_Accept_Charset = "Accept-Charset";
    public static final String REQUEST_HEADER_Accept_Encoding = "Accept-Encoding";
    public static final String REQUEST_HEADER_Connection = "Connection";
    public static final String REQUEST_HEADER_Content_Type = "Content-Type";

    private String status;

    private Map<String,String> header = new HashMap<String,String>();

    private StringBuilder headerBuff = new StringBuilder();
    private String method;

    private String firstLine;
    private String charset = "UTF-8";

    private String body;

    private String time = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss:ssss").format(new Date());

    public DeviceHttpRequest(byte[] buff,int length){
        if(buff == null || length <1){
            return ;
        }
        ByteArrayInputStream byteArrayInputStream = new ByteArrayInputStream(buff,
                0, length);
        try {
            firstLine = IO.readStringLine(byteArrayInputStream, charset);
            headerBuff.append(firstLine);

            String line = IO.readStringLine(byteArrayInputStream,charset);
            while(StringUtils.hasText(line)){
                headerBuff.append(line);
                String[] split1 = line.split(": ");
                if(split1.length ==2){
                    header.put(split1[0],split1[1]);
                }
                line = IO.readStringLine(byteArrayInputStream,charset);
            }
            String contentType = header.get(DeviceHttpRequest.REQUEST_HEADER_Content_Type);
            if(contentType == null){
                body = "response stream can not parse,ignore ....";
                return ;
            }
            String[] split1 = contentType.split(";");
            String contentT = split1[0].toLowerCase();
            if(!contentT.startsWith("text")&&!contentT.startsWith("application/json") &&!contentT.startsWith("application/xml")&&!contentT.startsWith("application/text") &&
                    !contentT.startsWith("application/x-www-form-urlencoded")){
                body = "request body is stream can not parse,ignore ....";
                return;
            }
            if(split1.length>1){
                String[] splitCharset = split1[1].split("=");
                if(splitCharset.length ==2){
                    charset = splitCharset[1].toUpperCase();
                }
            }
            body = IO.readAsText(byteArrayInputStream,charset);
            try {
                if(body!=null) {
                    body = java.net.URLDecoder.decode(body, charset);
                }
            }catch(Exception e){

            }
        }catch(Exception e){
            log.error("",e);
        }
    }


    public String getMethod() {
        return method;
    }

    public void setMethod(String method) {
        this.method = method;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public Map<String, String> getHeader() {
        return header;
    }

    public void setHeader(Map<String, String> header) {
        this.header = header;
    }

    public String toString(){
        StringBuilder buf = new StringBuilder();
        buf.append("Request Header:").append("[time:]").append(time).append(ProxyDispatch.CRLF);
        buf.append(headerBuff);
        buf.append(ProxyDispatch.CRLF);
        buf.append("Request Body:").append(ProxyDispatch.CRLF).append(body);
        return buf.toString();
    }

    public StringBuilder getHeaderBuff() {
        return headerBuff;
    }

    public void setHeaderBuff(StringBuilder headerBuff) {
        this.headerBuff = headerBuff;
    }

    public String getFirstLine() {
        return firstLine;
    }

    public void setFirstLine(String firstLine) {
        this.firstLine = firstLine;
    }

    public String getCharset() {
        return charset;
    }

    public void setCharset(String charset) {
        this.charset = charset;
    }

    public String getBody() {
        return body;
    }

    public void setBody(String body) {
        this.body = body;
    }


}
