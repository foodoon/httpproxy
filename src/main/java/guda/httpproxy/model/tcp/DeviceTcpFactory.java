package guda.httpproxy.model.tcp;

import guda.httpproxy.model.PoolQueue;

import java.util.*;

/**
 * Created by foodoon on 2014/12/7.
 */
public class DeviceTcpFactory {

    private static Map<String, PoolQueue<DeviceTcpPacket>> packetMap = new HashMap<String, PoolQueue<DeviceTcpPacket>>();


    public static void addSendPacket(DeviceTcpPacket deviceTcpPacket) {
        if (deviceTcpPacket == null) {
            return;
        }
        deviceTcpPacket.setDirection(DeviceTcpPacket.DIRECTION_SEND);
        PoolQueue<DeviceTcpPacket> deviceTcpPoolQueue = packetMap.get(deviceTcpPacket.getDeviceHost());
        if (deviceTcpPoolQueue == null) {
            deviceTcpPoolQueue = new PoolQueue<DeviceTcpPacket>();
            deviceTcpPoolQueue.add(deviceTcpPacket);
            packetMap.put(deviceTcpPacket.getDeviceHost(), deviceTcpPoolQueue);
        }
        deviceTcpPoolQueue.add(deviceTcpPacket);
    }

    public static void addRecePacket(DeviceTcpPacket deviceTcpPacket) {
        if (deviceTcpPacket == null) {
            return;
        }
        deviceTcpPacket.setDirection(DeviceTcpPacket.DIRECTION_RECV);
        PoolQueue<DeviceTcpPacket> deviceTcpPoolQueue = packetMap.get(deviceTcpPacket.getDeviceHost());
        if (deviceTcpPoolQueue == null) {
            deviceTcpPoolQueue = new PoolQueue<DeviceTcpPacket>();
            deviceTcpPoolQueue.add(deviceTcpPacket);
            packetMap.put(deviceTcpPacket.getDeviceHost(), deviceTcpPoolQueue);
        }
        deviceTcpPoolQueue.add(deviceTcpPacket);
    }

    public static void remove(String host) {
        packetMap.remove(host);
    }

    public static Set<String> getAllDevice() {
        return packetMap.keySet();
    }


    public static List<DeviceTcpPacket> get(String host) {
        PoolQueue<DeviceTcpPacket> deviceTcpPoolQueue = packetMap.get(host);
        if (deviceTcpPoolQueue == null) {
            return Collections.EMPTY_LIST;
        }
        return deviceTcpPoolQueue.peekAll();
    }

}
