package guda.httpproxy.biz;

import guda.httpproxy.model.DeviceHttpFactory;
import guda.httpproxy.model.DeviceHttpContext;
import guda.httpproxy.model.tcp.DeviceTcpFactory;
import guda.httpproxy.model.tcp.DeviceTcpPacket;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Set;

/**
 * Created by well on 2014/12/6.
 */
@Component
public class DeviceBiz {

    public Set<String> findDeviceList(){
        return DeviceHttpFactory.getAllDevice();
    }


    public List<DeviceHttpContext> findHttpRequestList(String host){
        return DeviceHttpFactory.get(host);
    }

    public List<DeviceTcpPacket> findTcpPacket(String host){
        return DeviceTcpFactory.get(host);
    }
}
