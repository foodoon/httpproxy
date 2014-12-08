/**
 * zoneland.net Inc.
 * Copyright (c) 2002-2012 All Rights Reserved.
 */
package guda.page;


import guda.httpproxy.watch.ProxyDispatch;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.io.support.PropertiesLoaderUtils;

import java.io.IOException;
import java.util.Properties;



/**
 * 
 * @author gag
 * @version $Id: JettyTestServer.java, v 0.1 2012-4-26 ����9:19:14 gag Exp $
 */
public class JettyServerStart extends JettyServer {

    public static Logger log = LoggerFactory.getLogger(JettyServerStart.class);

    public static void main(String[] args)  {
        try {
            Properties properties = PropertiesLoaderUtils.loadAllProperties("app.properties");
            try {
                int port = Integer.parseInt(properties.getProperty("proxy.port"));
                new ProxyDispatch(port);
                log.info("proxy start on " + port);
            } catch (IOException ioe) {
                log.error("Couldn't start server:\n", ioe);
                System.exit(-1);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        JettyServer jetty = new JettyServer();
        try {
            jetty.start();
        } catch (Exception e) {
            e.printStackTrace();
        }


    }
}
