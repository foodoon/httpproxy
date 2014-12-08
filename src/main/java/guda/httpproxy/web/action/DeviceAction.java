package guda.httpproxy.web.action;

import guda.httpproxy.biz.DeviceBiz;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import java.util.Set;

/**
 * Created by well on 2014/12/6.
 */
@Controller
public class DeviceAction {

    private static Logger logger = LoggerFactory.getLogger(DeviceAction.class);

    @Resource
    private DeviceBiz deviceBiz;

    @RequestMapping(value = "device/list.htm", method = RequestMethod.GET)
    public String list(HttpServletRequest request, ModelMap modelMap) {
        Set<String> deviceList = deviceBiz.findDeviceList();
        modelMap.addAttribute("deviceList", deviceList);
        return "device/list.vm";

    }

    @RequestMapping(value = "device/requestList.htm", method = RequestMethod.GET)
    public String requestlist(HttpServletRequest request, ModelMap modelMap) {
        String host = request.getParameter("host");
        modelMap.addAttribute("device",host);
        modelMap.addAttribute("requestlist", deviceBiz.findHttpRequestList(host));
        return "device/requestList.vm";


    }

    @RequestMapping(value = "device/clean.htm", method = RequestMethod.GET)
    public String clean(HttpServletRequest request, ModelMap modelMap) {
        String host = request.getParameter("host");
        modelMap.addAttribute("device",host);
        modelMap.addAttribute("requestlist", deviceBiz.clean(host));
        return "device/requestList.vm";


    }


    @RequestMapping(value = "device/packetList.htm", method = RequestMethod.GET)
    public String packetList(HttpServletRequest request, ModelMap modelMap) {
        String host = request.getParameter("host");


        modelMap.addAttribute("packetlist", deviceBiz.findTcpPacket(host));
        return "device/packetlist.vm";


    }
}
