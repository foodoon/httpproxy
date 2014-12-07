/**
 * zoneland.net Inc.
 * Copyright (c) 2002-2012 All Rights Reserved.
 */
package guda.httpproxy.web.action;

import javax.servlet.http.HttpServletRequest;


import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

/**
 *
 * 
 * @author gag
 * @version $Id: BlankController.java, v 0.1 2012-4-26 9:16:33 gag Exp $
 */
@Controller
@RequestMapping("/*.htm")
public class BlankAction {

    private final static Logger logger = LoggerFactory.getLogger(BlankAction.class);

    @RequestMapping(method = RequestMethod.GET)
    public String doGet(HttpServletRequest request, ModelMap modelMap) {
        String requestURI = request.getRequestURI();
        String contextPath = request.getContextPath();
        if("/".equals(contextPath)){
            return requestURI.substring(0,requestURI.length()-4) + ".vm";
        }else{
            return requestURI.substring(contextPath.length(),requestURI.length()-4)+ ".vm";
        }

    }


}
