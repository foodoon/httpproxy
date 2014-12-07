package guda.httpproxy.watch;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.Socket;

/**
 * Created by foodoon on 2014/12/7.
 */
public class ProcessTcp implements Runnable {

    public static final Logger log = LoggerFactory.getLogger(ProcessTcp.class);

    public static final int packetHeaderLength = 5;

    private Socket clientSocket;
    private OutputStream clientOutputStream ;
    private byte[] firstReadedBuf;
    private int firstReadedLength;
    private String tcpTargetHost;
    private int tcpTargetPort;
    private int clientbufsize = 8192;

    public ProcessTcp(Socket s, byte[] buf,int len,String tcpHost,int tcpPort) {
         clientSocket = s;
        firstReadedBuf = buf;
        firstReadedLength = len;
        tcpTargetHost = tcpHost;
        tcpTargetPort = tcpPort;
        Thread t = new Thread(this);
        t.setDaemon(true);
        t.start();
    }

    @Override
    public void run() {
        InputStream inputStream = null;
        OutputStream outputStream = null;
        InputStream clientInputStream = null;
        OutputStream clientOutputStream = null;
        Socket targetSocket = null;
        try {
            targetSocket = new Socket(tcpTargetHost, tcpTargetPort);
            if (targetSocket == null || !targetSocket.isConnected()) {
                throw new Exception("server: " + tcpTargetHost + " can't connect service");
            }
             inputStream = targetSocket.getInputStream();
             outputStream = targetSocket.getOutputStream();
            Thread targetThread = new Thread(new PipeReceiver(targetSocket, clientSocket.getOutputStream()));
            targetThread.setDaemon(true);
            targetThread.start();
            do {
                if(firstReadedLength < packetHeaderLength) {
                    int len = clientInputStream.read(firstReadedBuf, 0, clientbufsize - firstReadedLength);
                    firstReadedLength += len;
                }
                int packetLength = findPacketLength(firstReadedBuf, firstReadedLength);
                clientInputStream = clientSocket.getInputStream();
                clientOutputStream = clientSocket.getOutputStream();

                int bufsize = firstReadedBuf.length;
                while (firstReadedLength < packetLength) {
                    int read = clientInputStream.read(firstReadedBuf, firstReadedLength, bufsize - firstReadedLength);
                    firstReadedLength += read;
                }
                byte[] requestPacket = new byte[packetLength];
                System.arraycopy(firstReadedBuf, 0, requestPacket, 0, packetLength);

                //reset buf
                firstReadedLength = packetLength - firstReadedLength;
                byte[] temp = new byte[firstReadedLength];
                System.arraycopy(firstReadedBuf, packetLength, temp, 0, firstReadedLength);
                firstReadedBuf = new byte[clientbufsize];
                System.arraycopy(temp, 0, firstReadedBuf, 0, firstReadedLength);
                //reset end
                
                outputStream.write(requestPacket);
                outputStream.flush();

            }while(!clientSocket.isClosed());
        }catch(Exception e){
            log.error("",e);
        }finally{
            if(outputStream!=null) {
                try {
                    outputStream.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
            if(inputStream!=null) {
                try {
                    inputStream.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
            if(clientInputStream!=null) {
                try {
                    clientInputStream.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
            if(clientOutputStream!=null) {
                try {
                    clientOutputStream.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
            if(targetSocket!=null){
                try {
                    targetSocket.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
            try {
                clientSocket.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    public class PipeReceiver implements Runnable{
        private OutputStream clientOutputStream;
        private Socket targetSocket;
        private int targetBufsize = 8192;
        int readLength = 0;
        byte[] readBuff = new byte[targetBufsize];
        public PipeReceiver(Socket s,OutputStream os){
            targetSocket  = s;
            clientOutputStream = os;
        }
        @Override
        public void run() {
            do{
                try {
                    InputStream targetInputStream = targetSocket.getInputStream();
                     if(readLength < packetHeaderLength) {
                          readLength = targetInputStream.read(readBuff, 0, targetBufsize);
                     }
                    int packetLength = findPacketLength(readBuff,readLength);
                    if(packetLength < packetHeaderLength){
                        throw new RuntimeException("packet length wrong" + packetHeaderLength);
                    }
                    while (readLength < packetLength) {
                        int len = targetInputStream.read(readBuff, readLength, targetBufsize - readLength);
                        readLength += len;
                    }

                    byte[] responsePacket = new byte[packetLength];
                    System.arraycopy(readBuff, 0, responsePacket, 0, packetLength);
                    //reset buf
                    readLength = packetLength - readLength;
                    byte[] temp = new byte[readLength];
                    System.arraycopy(readBuff, packetLength, temp, 0, readLength);
                    readBuff = new byte[targetBufsize];
                    System.arraycopy(temp, 0, readBuff, 0, readLength);
                    //reset end
                    clientOutputStream.write(responsePacket);
                    clientOutputStream.flush();
                }catch(Exception e){
                    log.error("",e);
                }
            }while(!targetSocket.isClosed());
        }
    }

    private int findPacketLength(final byte[] buf,int rlen){
        //检查是否是socket
        if(rlen >4){
            byte[] length = new byte[packetHeaderLength];
            System.arraycopy(buf,0,length,0,packetHeaderLength);

            try {
                String len  = new String(length,ProxyDispatch.defaultCharset);
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
