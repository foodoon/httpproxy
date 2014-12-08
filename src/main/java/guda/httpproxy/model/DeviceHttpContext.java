package guda.httpproxy.model;

import guda.httpproxy.watch.ProxyDispatch;

/**
 * Created by well on 2014/12/6.
 */
public class DeviceHttpContext {

    private String deviceHost;

    private String requestString;



    public String getRequestString() {
        return requestString;
    }

    public void setRequestString(String requestString) {
        this.requestString = requestString;
    }

    private DeviceHttpRequest deviceHttpRequest;

    private DeviceHttpResponse deviceHttpResponse;

    public String getDeviceHost() {
        return deviceHost;
    }

    public void setDeviceHost(String deviceHost) {
        this.deviceHost = deviceHost;
    }

    public DeviceHttpRequest getDeviceHttpRequest() {
        return deviceHttpRequest;
    }

    public void setDeviceHttpRequest(DeviceHttpRequest deviceHttpRequest) {
        this.requestString = deviceHttpRequest.getFirstLine();
        this.deviceHttpRequest = deviceHttpRequest;
    }

    public DeviceHttpResponse getDeviceHttpResponse() {
        return deviceHttpResponse;
    }

    public void setDeviceHttpResponse(DeviceHttpResponse deviceHttpResponse) {
        this.deviceHttpResponse = deviceHttpResponse;
    }

    public String toString(){
        StringBuilder buf = new StringBuilder();
        buf.append(getDeviceHttpRequest()).append( ProxyDispatch.CRLF).append(getDeviceHttpResponse());
        return buf.toString();
    }

    public DeviceHttpContext copy(){
        DeviceHttpContext context = new DeviceHttpContext();
        context.setDeviceHttpResponse(this.deviceHttpResponse);
        context.setDeviceHost(this.deviceHost);
        context.setDeviceHttpRequest(this.deviceHttpRequest);
        context.setRequestString(this.requestString);
        return context;
    }


}
