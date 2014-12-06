package guda.httpproxy.biz;

import guda.httpproxy.model.DeviceFactory;
import guda.httpproxy.model.DeviceHttpContext;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Set;

/**
 * Created by well on 2014/12/6.
 */
@Component
public class DeviceBiz {

    public Set<String> findDeviceList(){
        return DeviceFactory.getAllDevice();
    }


    public List<DeviceHttpContext> findRequestList(String host){
        return DeviceFactory.get(host);
    }
}
