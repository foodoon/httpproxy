package guda.httpproxy.util;

/**
 * Created by foodoon on 2014/12/7.
 */
public class ByteUtil {

    public static int getInt(byte[] bytes){
        if(bytes == null){
            return 0;
        }
        String len = new String(bytes);
        try {
            return Integer.parseInt(len);
        }catch(Exception e){

        }
        return 0;

    }

    public static String getString(byte[] bytes){
        if(bytes == null){
            return null;
        }
        return new String(bytes);
    }

}
