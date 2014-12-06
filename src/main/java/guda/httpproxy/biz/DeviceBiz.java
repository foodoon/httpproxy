package guda.httpproxy.biz;

import guda.httpproxy.model.DeviceFactory;
import org.springframework.stereotype.Component;

import java.util.Set;

/**
 * Created by well on 2014/12/6.
 */
@Component
public class DeviceBiz {

    public Set<String> findDeviceList(){
        return DeviceFactory.getAllDevice();
    }
}
