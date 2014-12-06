package guda.httpproxy.web.action;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import javax.servlet.http.HttpServletRequest;

/**
 * Created by well on 2014/12/6.
 */
@Controller
public class IndexAction {

    private static Logger logger = LoggerFactory.getLogger(IndexAction.class);

    @RequestMapping(value="index.htm",method = RequestMethod.GET)
    public String doGet(HttpServletRequest request, ModelMap modelMap) {
        if (logger.isInfoEnabled()) {
            logger.info("test url" + request.getRequestURL());
        }
        return "index.vm";

    }
}
