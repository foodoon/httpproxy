package guda.httpproxy.Interceptor;

import guda.httpproxy.model.DeviceHttpContext;
import guda.httpproxy.watch.HttpWatch;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Created by well on 2014/12/6.
 */
public class LogRequestInterceptor implements RequestInterceptor {

    public static final Logger log = LoggerFactory.getLogger(LogRequestInterceptor.class);

    @Override
    public void on(DeviceHttpContext deviceHttpContext) {
        log.info(deviceHttpContext.getDeviceHost() + "request:" + deviceHttpContext.getRequestString());
        String requestString = deviceHttpContext.getRequestString();
        if (requestString == null) {
            return;
        }
        //String[] split = requestString.split(HttpWatch.CRLF);

    }
}
