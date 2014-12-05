package guda.httpproxy.watch;

import javax.net.ssl.*;
import java.io.*;
import java.security.KeyStore;
import java.security.cert.CertificateException;
import java.security.cert.X509Certificate;

/**
 * Created by well on 2014/12/5.
 */
public class CertManager {

    public static void main(String[] args) {
        try {
            trustCert( "www.alipay.com", 443);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    public static SSLSocketFactory trustCert( String host, int port) throws Exception {

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
        context.init(null, new TrustManager[]{tm}, null);

        // 尝试使用socket对目标主机进行通信
        SSLSocketFactory factory = context.getSocketFactory();
        SSLSocket socket = (SSLSocket) factory.createSocket(host, port);
        socket.setSoTimeout(1000);
        try {
            // 如果直接通信没问题的话,就不会报错,也不必获取证书
            // 如果报错的话,很有可能没有证书
            socket.startHandshake();
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (socket != null) {
                try {
                    socket.close();
                } catch (Exception e) {
                }
                socket = null;
            }
            X509Certificate[] chain = tm.getChain();
            if (chain != null) {
                System.out.println("服务器返回：" + chain.length + " 个证书");
                OutputStream out = null;
                for (int i = 0; i < chain.length; i++) {
                    try {
                        X509Certificate x509Cert = chain[i];
                        String alias = host + (i > 0 ? i + "" : "");
                        ks.setCertificateEntry(alias, x509Cert);

                        String certFile = dir +File.separator+ alias + ".cer";
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
        }
        return factory;
    }

    /**
     * 120      * 确保文件存在
     * 121      * @param file
     * 122      * @return
     * 123
     */
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
