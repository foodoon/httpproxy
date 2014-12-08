package guda.httpproxy.watch;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;

/**
 * Created by well on 2014/12/5.
 */
public class ProxyDispatch {

    public static final Logger log = LoggerFactory.getLogger(ProxyDispatch.class);

    int proxyTcpPort = 8080;
    private ServerSocket proxyServerSocket;
    private Thread proxyThread;
    public static final  String defaultCharset = "UTF-8";

    public static String CRLF = System.getProperty("line.separator");

    private String tcpTargetHost;
    private int tcpTargetPort;

    public ProxyDispatch(int port,String tcpHost,int tcpPort) throws IOException {
        tcpTargetHost = tcpHost;
        tcpTargetPort = tcpPort;
        proxyTcpPort = port;
        proxyServerSocket = new ServerSocket(proxyTcpPort);
        proxyThread = new Thread(new Runnable() {
            public void run() {
                try {
                    while (true)
                        new Dispatch(proxyServerSocket.accept());
                } catch (IOException ioe) {
                }
            }
        });
        proxyThread.setDaemon(true);
        proxyThread.start();
    }

    public ProxyDispatch(int port) throws IOException {
        proxyTcpPort = port;
        proxyServerSocket = new ServerSocket(proxyTcpPort);
        proxyThread = new Thread(new Runnable() {
            public void run() {
                try {
                    while (true)
                        new Dispatch(proxyServerSocket.accept());
                } catch (IOException ioe) {
                }
            }
        });
        proxyThread.setDaemon(true);
        proxyThread.start();
    }

    /**
     * Stops the server.
     */
    public void stop() {
        try {
            proxyServerSocket.close();
            proxyThread.join();
        } catch (IOException ioe) {
        } catch (InterruptedException e) {
        }
    }

    public class Dispatch implements Runnable {
        private Socket clientSocket;

        public Dispatch(Socket s) {
            clientSocket = s;
            Thread t = new Thread(this);
            t.setDaemon(true);
            t.start();
        }

        @Override
        public void run() {
            try {
                InputStream is = clientSocket.getInputStream();
                if (is == null)
                    return;
                final int bufsize = 8192;
                byte[] buf = new byte[bufsize];
                int splitbyte = 0;
                int rlen = 0;

                int read = is.read(buf, 0, bufsize);
                boolean isTcp = false;
                while (read > 0) {
                    rlen += read;
                    if(findPacketLength(buf,rlen)> 0){
                        //is tcp protocal
                        log.info("it is tcp...");
                        Thread thread = new Thread(new ProcessTcp(clientSocket, buf, rlen, tcpTargetHost, tcpTargetPort));
                        thread.setDaemon(true);
                        thread.start();
                        isTcp = true;
                        break;
                    }
                    splitbyte = findHeaderEnd(buf, rlen);
                    if (splitbyte > 0) {
                        break;
                    }
                    read = is.read(buf, rlen, bufsize - rlen);
                }
                if(!isTcp) {
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
                    if (!flag) {
                        clientSocket.getOutputStream().write(
                                "error!".getBytes());
                        clientSocket.close();
                        return;
                    }
                    new Thread(new ProcessHttp(clientSocket, buf, host, requestBuff, rlen)).start();
                }
            }catch (Exception e){
                 log.error("",e);
            }
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



        private int findPacketLength(final byte[] buf,int rlen){
            //检查是否是socket
            if(rlen >4){
                byte[] length = new byte[5];
                System.arraycopy(buf,0,length,0,5);

                try {
                    String len  = new String(length,defaultCharset);
                    int i = Integer.parseInt(len);
                    if (i > 0) {
                        return i;
                    }
                }catch(Exception e){

                }

            }
            return 0;
        }
    }

    public static void main(String[] args) {
        try {
            new ProxyDispatch(7272);
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
