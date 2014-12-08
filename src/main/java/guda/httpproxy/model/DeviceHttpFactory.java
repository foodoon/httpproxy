package guda.httpproxy.model;

import java.util.*;

/**
 * Created by well on 2014/12/6.
 */
public class DeviceHttpFactory {

    private static LinkedHashMap<String, PoolQueue<DeviceHttpContext>> deviceRequestMap = new LinkedHashMap<String, PoolQueue<DeviceHttpContext>>();

    private int maxDeviceCount = 100;

    public synchronized static void add(DeviceHttpContext deviceHttpContext) {
        if (deviceRequestMap.size() > 100) {
            String next = deviceRequestMap.keySet().iterator().next();
            deviceRequestMap.remove(next);
        }
        PoolQueue<DeviceHttpContext> deviceHttpContextPoolQueue = deviceRequestMap.get(deviceHttpContext.getDeviceHost());
        if (deviceHttpContextPoolQueue == null) {
            deviceHttpContextPoolQueue = new PoolQueue<DeviceHttpContext>();
            deviceHttpContextPoolQueue.add(deviceHttpContext);
            deviceRequestMap.put(deviceHttpContext.getDeviceHost(), deviceHttpContextPoolQueue);
        }
        deviceHttpContextPoolQueue.add(deviceHttpContext);
    }


    public static Set<String> getAllDevice() {
        return deviceRequestMap.keySet();
    }


    public static List<DeviceHttpContext> get(String host) {
        PoolQueue<DeviceHttpContext> deviceHttpContextPoolQueue = deviceRequestMap.get(host);
        if (deviceHttpContextPoolQueue == null) {
            return Collections.EMPTY_LIST;
        }
        return deviceHttpContextPoolQueue.peekAll();
    }

    public static List<DeviceHttpContext> clean(String host) {
        PoolQueue<DeviceHttpContext> deviceHttpContextPoolQueue = deviceRequestMap.get(host);
        deviceHttpContextPoolQueue = new PoolQueue<DeviceHttpContext>();
        return Collections.emptyList();
    }

}
