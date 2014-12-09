package guda.httpproxy.watch;

import guda.httpproxy.Interceptor.Interceptor;
import guda.httpproxy.Interceptor.LogResponseInterceptor;
import guda.httpproxy.model.DeviceHttpContext;
import guda.httpproxy.model.DeviceHttpFactory;
import guda.httpproxy.model.DeviceHttpRequest;
import guda.httpproxy.model.DeviceHttpResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.net.ssl.*;
import java.io.*;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.security.KeyStore;
import java.security.SecureRandom;
import java.security.cert.CertificateException;
import java.security.cert.X509Certificate;

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
                if (host.ssl) {
                    pipeSSL(firstReadedBuf, firstReadedLength, clientSocket.getInputStream(),
                            clientSocket.getOutputStream(), host);
                } else {
                pipe(firstReadedBuf, firstReadedLength, clientSocket.getInputStream(),
                        clientSocket.getOutputStream(), host);
                }
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
                DeviceHttpFactory.add(deviceHttpContext);
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
                        DeviceHttpFactory.fillResponse(deviceHttpContext.getId(),deviceHttpResponse,deviceHttpContext);

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


    void pipeSSL(byte[] request, int requestLen,
              InputStream clientIS, OutputStream clientOS, Host host)
            throws Exception {
        byte bytes[] = new byte[1024 * 32];


        //
        String dir = System.getProperty("user.dir");
        String password = "changeit";
        HostnameVerifier hv = new HostnameVerifier() {
            @Override
            public boolean verify(String urlHostName, SSLSession session) {
                return urlHostName.equals(session.getPeerHost());
            }
        };
        HttpsURLConnection.setDefaultHostnameVerifier(hv);

        // 信任管理器工厂
        TrustManagerFactory tmf = TrustManagerFactory.getInstance(TrustManagerFactory.getDefaultAlgorithm());
        File file = new File(dir + host + ".cer");
        file = makeSureFile(file);
        KeyStore ks = getKeyStore(file, password);
        tmf.init(ks);

        SSLContext context = SSLContext.getInstance("SSL");
        X509TrustManager defaultTrustManager = (X509TrustManager) tmf.getTrustManagers()[0];
        SavingTrustManager tm = new SavingTrustManager(defaultTrustManager);
        context.init(null, new TrustManager[]{tm}, new SecureRandom());
        X509Certificate[] chain = tm.getChain();
        if (chain != null) {
            OutputStream out = null;
            for (int i = 0; i < chain.length; i++) {
                try {
                    X509Certificate x509Cert = chain[i];
                    String alias = host + (i > 0 ? i + "" : "");
                    ks.setCertificateEntry(alias, x509Cert);

                    String certFile = dir + File.separator + alias + ".cer";
                    out = new FileOutputStream(certFile);
                    ks.store(out, password.toCharArray());
                    out.close();

                    System.setProperty("javax.net.ssl.trustStore", certFile);
                    System.out.println("第" + (i + 1) + "个证书安装成功,path:" + certFile);
                } catch (Exception e) {
                    e.printStackTrace();
                    continue;
                } finally {
                    try {
                        if (out != null) {
                            out.close();
                        }
                        out = null;
                    } catch (Exception e) {
                    }
                }
            }
        }

       // SSLContext context = SSLContext.getInstance("SSL");
      //  context.init(null, new TrustManager[]{new TrustAnyTrustManager()}, new SecureRandom());
        Socket socket = context.getSocketFactory().createSocket(host.address, host.port);
       // Socket socket = new Socket(host.address, host.port);
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
                        DeviceHttpContext copy = deviceHttpContext.copy();
                        DeviceHttpResponse deviceHttpResponse = new DeviceHttpResponse(bytes);
                        copy.setDeviceHttpResponse(deviceHttpResponse);
                        responseInterceptor.on(copy);

                    }
                } catch (Exception e) {
                    log.error("target Socket exception:address:"+  host.address+  ",port" + host.port+
                         e.getMessage());
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

    class TrustAnyTrustManager implements X509TrustManager {
        public void checkClientTrusted(X509Certificate[] chain, String authType)
                throws CertificateException {
        }

        public void checkServerTrusted(X509Certificate[] chain, String authType)
                throws CertificateException {
        }

        public X509Certificate[] getAcceptedIssuers() {
            return new X509Certificate[]{};
        }
    }


    class TrustAnyHostnameVerifier implements HostnameVerifier {
        public boolean verify(String hostname, SSLSession session) {
            return true;
        }
    }

    private static File makeSureFile(File file) {
        if (file.isFile() == false) {
            char SEP = File.separatorChar;
            File dir = new File(System.getProperty("user.dir"));
            file = new File(dir, file.getName());
            if (file.isFile() == false) {
                file = new File(dir, "cacerts");
            }
        }
        return file;
    }

    /**
     * 获取keystore
     *
     * @param file
     * @param password
     * @return
     * @throws Exception
     */
    private static KeyStore getKeyStore(File file, String password) throws Exception {
        InputStream in = new FileInputStream(file);
        KeyStore ks = KeyStore.getInstance(KeyStore.getDefaultType());
        char[] passphrase = password.toCharArray();
        ks.load(in, passphrase);
        in.close();
        return ks;
    }

    public static class SavingTrustManager implements X509TrustManager {
        private final X509TrustManager tm;
        private X509Certificate[] chain;

        public SavingTrustManager(X509TrustManager tm) {
            this.tm = tm;
        }

        public X509TrustManager getTM() {
            return tm;
        }

        public X509Certificate[] getChain() {
            return chain;
        }

        public X509Certificate[] getAcceptedIssuers() {
            throw new UnsupportedOperationException();
        }

        public void checkClientTrusted(X509Certificate[] chain, String authType)
                throws CertificateException {
            throw new UnsupportedOperationException();
        }

        public void checkServerTrusted(X509Certificate[] chain, String authType)
                throws CertificateException {
            this.chain = chain;
            tm.checkServerTrusted(chain, authType);
        }
    }


}
