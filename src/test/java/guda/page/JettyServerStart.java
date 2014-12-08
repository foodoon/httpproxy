/**
 * zoneland.net Inc.
 * Copyright (c) 2002-2012 All Rights Reserved.
 */
package guda.page;


import guda.httpproxy.watch.ProxyDispatch;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;

/**
 * 
 * @author gag
 * @version $Id: JettyTestServer.java, v 0.1 2012-4-26 ����9:19:14 gag Exp $
 */
public class JettyServerStart extends JettyServer {

    public static Logger log = LoggerFactory.getLogger(JettyServerStart.class);

    public static void main(String[] args)  {
        try {
            new ProxyDispatch(7272);
        } catch (IOException ioe) {
            log.error("Couldn't start server:\n", ioe);
            System.exit(-1);
        }
        JettyServer jetty = new JettyServer();
        try {
            jetty.start();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
