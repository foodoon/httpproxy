package guda.httpproxy.web.action;

import guda.httpproxy.util.AppPropertiesUtil;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import javax.servlet.http.HttpServletRequest;
import java.net.InetAddress;

/**
 * Created by well on 2014/12/8.
 */
@Controller
public class HelpAction {

    @RequestMapping(value="help.htm",method = RequestMethod.GET)
    public String doGet(HttpServletRequest request, ModelMap modelMap) {
        try {
            InetAddress addr = InetAddress.getLocalHost();
            String host = addr.getHostAddress().toString();

            modelMap.addAttribute("host",host);
        }catch(Exception e){

        }
        modelMap.addAttribute("port", AppPropertiesUtil.getAppProperties("proxy.port"));
        return "help.vm";

    }
}
