package guda.httpproxy.Interceptor;

import guda.httpproxy.model.DeviceHttpContext;

/**
 * Created by well on 2014/12/6.
 */
public interface Interceptor {

    public void on(DeviceHttpContext deviceHttpContext);

}
