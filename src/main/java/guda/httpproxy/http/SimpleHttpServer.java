package guda.httpproxy.http;


import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.ServerSocket;
import java.net.Socket;


public class SimpleHttpServer {
    private static final Logger logger = LoggerFactory.getLogger(SimpleHttpServer.class);

    public static void main(String[] args) {
        ServerSocket serverSocket = null;

        Socket socket = null;
        try {
            serverSocket = new ServerSocket(8081);
            logger.info("start server......");
            while (true) {
                Proxy proxy = new SimpleHttpServer.Proxy(serverSocket.accept());
                Thread thread  = new Thread(proxy);
                thread.start();
                logger.info("accept......");
            }


        } catch (Exception e) {
            logger.error("", e);
        }
    }

    public static class Proxy implements Runnable {
        private Socket socket;

        public Proxy(Socket socket) {
            this.socket = socket;
        }

        @Override
        public void run() {
            try {
                InputStream in = socket.getInputStream();
                OutputStream out = socket.getOutputStream();
                byte[] bytes = null;
                try {
                    StringBuilder buf = new StringBuilder();
                    while ((bytes = readLine(in)) != null) {
                        String headers = new String(bytes);
                        buf.append(headers);
                        if ("\r\n".equals(headers)) {
                            break;
                        }
                    }
                    logger.info("request:"+buf.toString());


                } catch (Exception e) {
                    e.printStackTrace();
                }
                out.write("HTTP/1.1 200 OK\r\n".getBytes());
                out.write("Content-Type: text/html;charset=ISO-8859-1\r\n".getBytes());
                out.write("Date: Wed, 25 Jul 2007 02:07:55 GMT\r\n".getBytes());
                out.write("Server: Apache-Coyote/1.1\r\n".getBytes());
                out.write("\r\n".getBytes());

                out.write("<html>".getBytes());
                out.write("<head>".getBytes());
                out.write("<title>Test</title>".getBytes());
                out.write("</head>".getBytes());
                out.write("<body>".getBytes());
                out.write("<h1>Hello World !</h1>".getBytes());
                out.write("</body>".getBytes());
                out.write("<html>".getBytes());

                out.flush();
            } catch (IOException e) {
                e.printStackTrace();
            } finally {
                if (socket != null) {
                    try {
                        socket.close();
                    } catch (IOException e) {
                    }
                }
            }
        }
    }


    public static byte[] readLine(InputStream stream) throws IOException {
        ByteArrayOutputStream bos = new ByteArrayOutputStream(4096);

        int b = -1;

        while ((b = stream.read()) != -1) {
            if (b == 10) {
                bos.write(b);
                break;
            }

            bos.write(b);
        }

        if (bos.size() < 1) {
            return null;
        }

        return bos.toByteArray();
    }

}

