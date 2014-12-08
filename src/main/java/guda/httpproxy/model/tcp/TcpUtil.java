package guda.httpproxy.model.tcp;

/**
 * Created by well on 2014/12/8.
 */
public class TcpUtil {

    public static Header decodeHeader(byte[] buff) {

        Header header = new Header();
        byte[] temp = new byte[4];
        System.arraycopy(buff,0,temp,0,4);
        header.setTotalLength(Integer.parseInt(new String(temp)));// 4
        temp = new byte[2];
        System.arraycopy(buff,4,temp,0,2);
        header.setHeaderLegth(Short.parseShort(new String(temp)));// 2
        temp = new byte[2];
        System.arraycopy(buff,6,temp,0,2);
        header.setProtocolVersion(Short.parseShort(new String(temp)));// 2
        temp = new byte[2];
        System.arraycopy(buff,8,temp,0,2);
        header.setMessageType(Short.parseShort(new String(temp)));// 2
        temp = new byte[4];
        System.arraycopy(buff,10,temp,0,4);
        header.setMessageId(Integer.parseInt(new String(temp)));// 4
        fillQosActionEncr(header, buff[14]);// 1
        return header;
    }

    public static void fillQosActionEncr(Header header, byte b) {
        byte qos = (byte) ((b >> 6) & (0x3));
        byte action = (byte) ((b >> 5) & 0x1);
        byte encrypt = (byte) ((b >> 1) & 0xf);
        header.setQosLevel(qos);
        header.setActionType(action);
        header.setEncryptType(encrypt);

    }

}
