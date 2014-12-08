package guda.httpproxy.watch;

import guda.httpproxy.Interceptor.Interceptor;
import guda.httpproxy.Interceptor.LogResponseInterceptor;
import guda.httpproxy.model.DeviceHttpContext;
import guda.httpproxy.model.DeviceHttpFactory;
import guda.httpproxy.model.DeviceHttpRequest;
import guda.httpproxy.model.DeviceHttpResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.net.ssl.SSLSocket;
import javax.net.ssl.SSLSocketFactory;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.InetSocketAddress;
import java.net.Socket;

/**
 * Created by foodoon on 2014/12/7.
 */
public class ProcessHttp implements Runnable {

    public static final Logger log = LoggerFactory.getLogger(ProcessHttp.class);

    private Interceptor responseInterceptor = new LogResponseInterceptor();

    static long threadCount = 0;
    private Socket clientSocket;
    private byte[] firstReadedBuf;
    Host host;
    StringBuilder requestBuf;
    int firstReadedLength;

    public ProcessHttp(Socket s, byte[] readed, Host h, StringBuilder buf, int len) {
        clientSocket = s;
        firstReadedBuf = readed;
        requestBuf = buf;
        firstReadedLength = len;
        host = h;

    }

    @Override
    public void run() {
        try {
            ++threadCount;
            InputStream is = clientSocket.getInputStream();
            if (is == null) {
                return;
            }


            host.cal();

            try {
//                if (host.ssl) {
//                    pipeSSL(firstReadedBuf, firstReadedLength, clientSocket.getInputStream(),
//                            clientSocket.getOutputStream(), host, deviceHttpContext);
//                } else {
                pipe(firstReadedBuf, firstReadedLength, clientSocket.getInputStream(),
                        clientSocket.getOutputStream(), host);
                //}
            } catch (Exception e) {
                log.error("Run Exception!", e);
            }

        } catch (Exception e) {
            log.error("Run Exception!", e);
        }
        log.info("threadcount:" + --threadCount);
    }


    void pipe(byte[] request, int requestLen,
              InputStream clientIS, OutputStream clientOS, Host host)
            throws Exception {
        byte bytes[] = new byte[1024 * 32];
        Socket socket = new Socket(host.address, host.port);
        socket.setSoTimeout(3000);
        OutputStream os = socket.getOutputStream();
        InputStream is = socket.getInputStream();
        try {
            do {
                log.info("request:" + new String(request));
                DeviceHttpContext deviceHttpContext = new DeviceHttpContext();
                DeviceHttpRequest deviceHttpRequest = new DeviceHttpRequest(request, requestLen);
                deviceHttpContext.setDeviceHttpRequest(deviceHttpRequest);
                deviceHttpContext.setDeviceHost(((InetSocketAddress) clientSocket.getRemoteSocketAddress()).getHostName());
                os.write(request, 0, requestLen);
                os.flush();
                int resultLen = 0;
                try {
                    while ((resultLen = is.read(bytes)) != -1
                            && !clientSocket.isClosed() && !socket.isClosed()) {
                        clientOS.write(bytes, 0, resultLen);
                        clientOS.flush();
                        log.info(Thread.currentThread().getName() + "request:" + new String(request) + ProxyDispatch.CRLF + "response:" + new String(bytes));
                        DeviceHttpResponse deviceHttpResponse = new DeviceHttpResponse(bytes);
                        deviceHttpContext.setDeviceHttpResponse(deviceHttpResponse);
                        responseInterceptor.on(deviceHttpContext);

                    }
                } catch (Exception e) {
                    //log.error("target Socket exception:address:"  host.address  ",port"  host.port
                    //     e.getMessage());
                }

            } while (!clientSocket.isClosed()
                    && (requestLen = clientIS.read(request)) != -1);

        } catch (Exception e) {
            //log.error("client Socket exception:", e);
        } finally {

            os.close();
            is.close();
            clientIS.close();
            clientOS.close();
            socket.close();
            clientSocket.close();
        }
    }


}
