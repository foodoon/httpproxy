package guda.httpproxy.model;

import java.util.*;

/**
 * Created by well on 2014/12/6.
 */
public class DeviceHttpFactory {

    private static LinkedHashMap<String, PoolQueue<DeviceHttpContext>> deviceRequestMap = new LinkedHashMap<String, PoolQueue<DeviceHttpContext>>();
    private static HashMap<UUID, DeviceHttpContext> request = new HashMap<UUID, DeviceHttpContext>();
    private static int maxDeviceCount = 100;

    public synchronized static void add(DeviceHttpContext deviceHttpContext) {
        if (deviceRequestMap.size() > maxDeviceCount) {
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
        request.put(deviceHttpContext.getId(),deviceHttpContext);
    }

    public  static void fillResponse(UUID uuid,DeviceHttpResponse response, DeviceHttpContext deviceHttpContext) {
        DeviceHttpContext deviceHttpContext1 = request.get(uuid);
        if(deviceHttpContext1 == null){
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
        deviceHttpContext1.setDeviceHttpResponse(response);
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
