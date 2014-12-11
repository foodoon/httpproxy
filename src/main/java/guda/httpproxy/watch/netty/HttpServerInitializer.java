package guda.httpproxy.watch.netty;


import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelPipeline;
import io.netty.channel.socket.SocketChannel;
import io.netty.handler.codec.http.HttpContentCompressor;
import io.netty.handler.codec.http.HttpRequestDecoder;
import io.netty.handler.codec.http.HttpResponseEncoder;
import io.netty.handler.ssl.SslHandler;

import javax.net.ssl.SSLEngine;

/**
 * Created by well on 2014/12/11.
 */
public class HttpServerInitializer extends ChannelInitializer<SocketChannel> {
    @Override
    public void initChannel(SocketChannel ch) throws Exception {

        ChannelPipeline pipeline = ch.pipeline();

//        if (HttpDemoServer.isSSL) {
//            SSLEngine engine = SSLEngine.getServerContext().createSSLEngine();
//            engine.setUseClientMode(false);
//            pipeline.addLast("ssl", new SslHandler(engine));
//        }

        pipeline.addLast("decoder", new HttpRequestDecoder());
        pipeline.addLast("encoder", new HttpResponseEncoder());

        pipeline.addLast("deflater", new HttpContentCompressor());

        pipeline.addLast("handler", new HttpServerHandler());
    }
}
