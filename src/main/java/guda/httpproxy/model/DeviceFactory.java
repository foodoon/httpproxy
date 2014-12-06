package guda.httpproxy.model;

import java.util.*;

/**
 * Created by well on 2014/12/6.
 */
public class DeviceFactory {

    private static Map<String, PoolQueue<DeviceHttpContext>> deviceRequestMap = new HashMap<String, PoolQueue<DeviceHttpContext>>();

    public static void add(DeviceHttpContext deviceHttpContext){
        PoolQueue<DeviceHttpContext> deviceHttpContextPoolQueue = deviceRequestMap.get(deviceHttpContext.getDeviceHost());
        if(deviceHttpContextPoolQueue == null){
            deviceHttpContextPoolQueue = new PoolQueue<DeviceHttpContext>();
            deviceHttpContextPoolQueue.add(deviceHttpContext);
            deviceRequestMap.put(deviceHttpContext.getDeviceHost(),deviceHttpContextPoolQueue);
        }
        deviceHttpContextPoolQueue.add(deviceHttpContext);
    }

    public static Set<String> getAllDevice(){
        return deviceRequestMap.keySet();
    }


    public static List<DeviceHttpContext> get(String host){
        PoolQueue<DeviceHttpContext> deviceHttpContextPoolQueue = deviceRequestMap.get(host);
        if(deviceHttpContextPoolQueue == null){
            return Collections.EMPTY_LIST;
        }
        return deviceHttpContextPoolQueue.peekAll();
    }

}
