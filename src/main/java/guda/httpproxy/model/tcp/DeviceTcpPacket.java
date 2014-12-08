package guda.httpproxy.model.tcp;

import com.alibaba.fastjson.JSON;
import guda.httpproxy.util.ByteUtil;
import guda.httpproxy.watch.ProcessTcp;
import guda.httpproxy.watch.ProxyDispatch;

/**
 * Created by foodoon on 2014/12/7.
 */
public class DeviceTcpPacket {

    public static final String DIRECTION_SEND = "send";
    public static final String DIRECTION_RECV = "receiver";

    private String direction;

    private String deviceHost;

    private int totalLength;

    private int headerLength;

    private String header;

    private String body;

    public DeviceTcpPacket(byte[] p){
        if(p == null){
            return;
        }
        byte[] packet = new byte[p.length];
        System.arraycopy(p,0,packet,0,p.length);
        totalLength = packet.length;
        byte[] headerLen = new byte[2];
        System.arraycopy(packet,4,headerLen,0,2);
        headerLength = ByteUtil.getInt(headerLen);
        if(headerLength  == 0){
            return;
        }
        byte[] headerByte = new byte[headerLength];
        System.arraycopy(packet,ProcessTcp.packetLength,headerLen,0,ProcessTcp.packetHeaderLength);
        header = JSON.toJSONString(TcpUtil.decodeHeader(headerByte));
        byte[] bodyByte = new byte[totalLength - headerLength- ProcessTcp.packetLength];
        System.arraycopy(packet,headerLength+ ProcessTcp.packetLength,bodyByte,0,bodyByte.length);
        body = ByteUtil.getString(bodyByte);
    }

    public String getDirection() {
        return direction;
    }

    public void setDirection(String direction) {
        this.direction = direction;
    }

    public String getDeviceHost() {
        return deviceHost;
    }

    public void setDeviceHost(String deviceHost) {
        this.deviceHost = deviceHost;
    }

    public int getTotalLength() {
        return totalLength;
    }

    public void setTotalLength(int totalLength) {
        this.totalLength = totalLength;
    }

    public int getHeaderLength() {
        return headerLength;
    }

    public void setHeaderLength(int headerLength) {
        this.headerLength = headerLength;
    }

    public String getHeader() {
        return header;
    }

    public void setHeader(String header) {
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
        buf.append(direction).append(" ").append("total-lenght:").append(totalLength).append(",header-length:").append(headerLength);
        buf.append(ProxyDispatch.CRLF).append("header:").append(header).append(ProxyDispatch.CRLF).append("body:").append(body);
        return buf.toString();
    }
}
