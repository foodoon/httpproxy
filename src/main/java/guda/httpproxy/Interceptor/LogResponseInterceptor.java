package guda.httpproxy.Interceptor;

import guda.httpproxy.model.DeviceFactory;
import guda.httpproxy.model.DeviceHttpContext;
import guda.httpproxy.watch.HttpWatch;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Created by foodoon on 2014/12/6.
 */
public class LogResponseInterceptor implements Interceptor {

    public static final Logger log = LoggerFactory.getLogger(LogResponseInterceptor.class);

    @Override
    public void on(DeviceHttpContext deviceHttpContext) {
        log.info("request" + HttpWatch.CRLF + deviceHttpContext.getRequestString() + HttpWatch.CRLF + "response:" + HttpWatch.CRLF + deviceHttpContext.getDeviceHttpResponse());
        DeviceFactory.add(deviceHttpContext);
    }
}
