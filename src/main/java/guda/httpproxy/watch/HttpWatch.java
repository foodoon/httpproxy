package guda.httpproxy.watch;

import guda.httpproxy.Interceptor.Interceptor;
import guda.httpproxy.Interceptor.LogRequestInterceptor;
import guda.httpproxy.Interceptor.LogResponseInterceptor;
import guda.httpproxy.model.DeviceHttpContext;
import guda.httpproxy.model.DeviceHttpRequest;
import guda.httpproxy.model.DeviceHttpResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.net.ssl.*;
import java.io.*;
import java.net.InetSocketAddress;
import java.net.ServerSocket;
import java.net.Socket;

/**
 * Created by well on 2014/12/5.
 */
public class HttpWatch {

    public static final Logger log = LoggerFactory.getLogger(HttpWatch.class);
    static long threadCount = 0;
    int myTcpPort = 8080;
    private ServerSocket myServerSocket;
    private Thread myThread;

    public static String CRLF = System.getProperty("line.separator");

    private Interceptor requestInterceptor = new LogRequestInterceptor();

    private Interceptor responseInterceptor = new LogResponseInterceptor();

    public HttpWatch(int port) throws IOException {
        myTcpPort = port;
        myServerSocket = new ServerSocket(myTcpPort);
        myThread = new Thread(new Runnable() {
            public void run() {
                try {
                    while (true)
                        new HTTPSession(myServerSocket.accept());
                } catch (IOException ioe) {
                }
            }
        });
        myThread.setDaemon(true);
        myThread.start();
    }

    /**
     * Stops the server.
     */
    public void stop() {
        try {
            myServerSocket.close();
            myThread.join();
        } catch (IOException ioe) {
        } catch (InterruptedException e) {
        }
    }

    public class HTTPSession implements Runnable {
        private Socket mySocket;

        public HTTPSession(Socket s) {
            mySocket = s;
            Thread t = new Thread(this);
            t.setDaemon(true);
            t.start();
        }

        @Override
        public void run() {
            try {
                ++threadCount;
                InputStream is = mySocket.getInputStream();
                if (is == null)
                    return;
                final int bufsize = 8192;
                byte[] buf = new byte[bufsize];
                int splitbyte = 0;
                int rlen = 0;

                int read = is.read(buf, 0, bufsize);
                while (read > 0) {
                    rlen += read;
                    splitbyte = findHeaderEnd(buf, rlen);
                    if (splitbyte > 0)
                        break;
                    read = is.read(buf, rlen, bufsize - rlen);
                }
                ByteArrayInputStream hbis = new ByteArrayInputStream(buf,
                        0, rlen);
                BufferedReader hin = new BufferedReader(
                        new InputStreamReader(hbis));
                Host host = new Host();

                String string;
                boolean flag = false;
                StringBuilder requestBuff = new StringBuilder();
                while ((string = hin.readLine()) != null) {
                    if (string.toLowerCase().startsWith("host:")) {
                        host.host = string;
                        flag = true;
                    }
                    checkSSL(string, host);
                    requestBuff.append(string).append(CRLF);

                }

                DeviceHttpContext deviceHttpContext = new DeviceHttpContext(requestBuff.toString());
                DeviceHttpRequest deviceHttpRequest = new DeviceHttpRequest(buf,rlen);
                deviceHttpContext.setDeviceHttpRequest(deviceHttpRequest);
                deviceHttpContext.setDeviceHost(((InetSocketAddress)mySocket.getRemoteSocketAddress()).getHostName());
               // requestInterceptor.on(deviceHttpContext);
                if (!flag) {
                    mySocket.getOutputStream().write(
                            "error!".getBytes());
                    mySocket.close();
                    return;
                }

                host.cal();

                try {
                    if (host.ssl) {
                        pipeSSL(buf, rlen, mySocket, mySocket.getInputStream(),
                                mySocket.getOutputStream(), host, deviceHttpContext);
                    } else {
                        pipe(buf, rlen, mySocket, mySocket.getInputStream(),
                                mySocket.getOutputStream(), host, deviceHttpContext);
                    }
                } catch (Exception e) {
                    log.error("Run Exception!", e);
                }

            } catch (Exception e) {
                log.error("Run Exception!", e);
            }
            log.info("threadcount:" + --threadCount);
        }

        private boolean checkSSL(String str, Host host) {
            if (str.startsWith("CONNECT ") && str.indexOf(":443")>-1) {
                host.ssl = true;
            }
            return false;
        }


        private int findHeaderEnd(final byte[] buf, int rlen) {
            int splitbyte = 0;
            while (splitbyte + 3 < rlen) {
                if (buf[splitbyte] == '\r' && buf[splitbyte + 1] == '\n'
                        && buf[splitbyte + 2] == '\r'
                        && buf[splitbyte + 3] == '\n')
                    return splitbyte + 4;
                splitbyte++;
            }
            return 0;
        }

        void pipe(byte[] request, int requestLen, Socket client,
                  InputStream clientIS, OutputStream clientOS, Host host,DeviceHttpContext deviceHttpContext)
                throws Exception {
            byte bytes[] = new byte[1024 * 32];
            Socket socket = new Socket(host.address, host.port);
            socket.setSoTimeout(3000);
            OutputStream os = socket.getOutputStream();
            InputStream is = socket.getInputStream();
            try {
                do {
                    os.write(request, 0, requestLen);
                    int resultLen = 0;
                    try {
                        while ((resultLen = is.read(bytes)) != -1
                                && !mySocket.isClosed() && !socket.isClosed()) {
                            // log.info("response:" + new String(bytes));
                            DeviceHttpResponse deviceHttpResponse = new DeviceHttpResponse(bytes);
                            deviceHttpContext.setDeviceHttpResponse(deviceHttpResponse);
                            responseInterceptor.on(deviceHttpContext);
                            clientOS.write(bytes, 0, resultLen);
                        }
                    } catch (Exception e) {
                        //log.error("target Socket exception:address:" + host.address + ",port" + host.port
                            //    + e.getMessage());
                    }

                } while (!mySocket.isClosed()
                        && (requestLen = clientIS.read(request)) != -1);
            } catch (Exception e) {
                //log.error("client Socket exception:", e);
            }

            os.close();
            is.close();
            clientIS.close();
            clientOS.close();
            socket.close();
            mySocket.close();
        }


        void pipeSSL(byte[] request, int requestLen, Socket client,
                     InputStream clientIS, OutputStream clientOS, Host host,DeviceHttpContext deviceHttpContext)
                throws Exception {
            SSLSocketFactory sslSocketFactory = CertManager.trustCert(host.address, host.port);
            SSLSocket socket = (SSLSocket) sslSocketFactory.createSocket(host.address, host.port);

            byte bytes[] = new byte[1024 * 32];
            // Socket socket = new Socket(host.address, host.port);
            socket.setSoTimeout(3000);
            OutputStream os = socket.getOutputStream();
            InputStream is = socket.getInputStream();
            try {
                do {
                    os.write(request, 0, requestLen);
                    int resultLen = 0;
                    try {
                        while ((resultLen = is.read(bytes)) != -1
                                && !mySocket.isClosed() && !socket.isClosed()) {
                            // log.info("response:" + new String(bytes));
                            clientOS.write(bytes, 0, resultLen);
                        }
                    } catch (Exception ex) {
                        log.error("target Socket exception:address:" + host.address + ",port" + host.port
                                + ex.getMessage());
                    }

                } while (!mySocket.isClosed()
                        && (requestLen = clientIS.read(request)) != -1);
            } catch (Exception e) {
                log.error("client Socket exception:", e);
            }

            os.close();
            is.close();
            clientIS.close();
            clientOS.close();
            socket.close();
            mySocket.close();
        }

        // target Host info
        final class Host {
            public String address;
            public int port;
            public String host;
            public boolean ssl;

            public boolean cal() {
                if (host == null)
                    return false;
                int start = host.indexOf(": ");
                if (start == -1)
                    return false;
                int next = host.indexOf(':', start + 2);
                if (next == -1) {
                    port = 80;
                    address = host.substring(start + 2);
                } else {
                    address = host.substring(start + 2, next);
                    port = Integer.valueOf(host.substring(next + 1));
                }
                if(ssl){
                    port = 443;
                }
                return true;
            }
        }
    }

    public static void main(String[] args) {
        try {
            new HttpWatch(7272);
        } catch (IOException ioe) {
            log.error("Couldn't start server:\n", ioe);
            System.exit(-1);
        }
        log.info("start!");
        try {
            System.in.read();
        } catch (Throwable t) {
        }
        log.info("stop!");
    }
}
