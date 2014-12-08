package guda.httpproxy.watch;

/**
 * Created by well on 2014/12/8.
 */
public class TcpProxyConfig {

    private static String host;

    private static int port;

    public static String getHost() {
        return host;
    }

    public void setHost(String host) {
        this.host = host;
    }

    public static int getPort() {
        return port;
    }

    public void setPort(int port) {
        this.port = port;
    }
}
