/**
 * zoneland.net Inc.
 * Copyright (c) 2002-2012 All Rights Reserved.
 */
package guda.page;



import guda.mvc.core.config.ConfigrationFactory;
import org.eclipse.jetty.server.Server;
import org.eclipse.jetty.server.handler.HandlerCollection;
import org.eclipse.jetty.webapp.WebAppContext;

import java.io.File;

/**
 * 
 * @author gag
 * @version $Id: JettyServer.java, v 0.1 2012-4-26 下午8:18:54 gag Exp $
 */
public class JettyServer {

    public static final int defaultJettyPort = 7777;

    private int             jettyPort        = defaultJettyPort;

    public void start() throws Exception {
        Server server = new Server(jettyPort);
        HandlerCollection collection = new HandlerCollection();
        collection.addHandler(createWebapp());
        server.setHandler(collection);
        server.start();
        server.join();
    }

    protected WebAppContext createWebapp() {
        WebAppContext webapp = new WebAppContext();
        webapp.setDescriptor(getWebDescriptor());
        webapp.setResourceBase(getAppRoot() + "/webapps");
        webapp.setContextPath("/");
        webapp.setInitParameter("org.eclipse.jetty.servlet.Default.dirAllowed", "false");
        webapp.setInitParameter("org.eclipse.jetty.servlet.Default.useFileMappedBuffer", "false");
        return webapp;
    }

    public void setJettyPort(int jettyPort) {
        this.jettyPort = jettyPort;
    }

    private String getWebDescriptor() {
        return getAppRoot() + File.separatorChar + "htdocs" + File.separatorChar + "home"
               + File.separatorChar + "WEB-INF"+File.separatorChar+"web.xml";
    }



    protected String getAppRoot() {
        return System.getProperty("user.dir");
    }



}
