package guda.httpproxy.model.tcp;

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
}
