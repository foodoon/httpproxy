package guda.httpproxy.model;

/**
 * Created by well on 2014/12/6.
 */
public class DeviceHttpContext {

    private String deviceHost;

    private String requestString;

    public DeviceHttpContext(String request){
        requestString = request;
    }

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
        this.deviceHttpRequest = deviceHttpRequest;
    }

    public DeviceHttpResponse getDeviceHttpResponse() {
        return deviceHttpResponse;
    }

    public void setDeviceHttpResponse(DeviceHttpResponse deviceHttpResponse) {
        this.deviceHttpResponse = deviceHttpResponse;
    }


}
