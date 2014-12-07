package guda.httpproxy.watch;

/**
 * Created by foodoon on 2014/12/7.
 */
public class Host {

    public String address;
    public int port;
    public String host;
    public boolean ssl;
    public boolean http;
    public boolean tcp;

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
