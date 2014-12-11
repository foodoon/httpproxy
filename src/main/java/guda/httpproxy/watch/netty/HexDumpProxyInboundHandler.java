package guda.httpproxy.watch.netty;

import io.netty.bootstrap.Bootstrap;
import io.netty.channel.*;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.nio.NioSocketChannel;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Created by well on 2014/12/11.
 */
public class HexDumpProxyInboundHandler extends ChannelInboundHandlerAdapter {

    public static final Logger log = LoggerFactory.getLogger(HexDumpProxyInboundHandler.class);
    @Override
    public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
        //解析 remote host  port
        log.info("recv :"+msg);
        String targetHost = null;
        int targetPort = 0;
        EventLoopGroup group = new NioEventLoopGroup(1);
        Bootstrap b = new Bootstrap();
        b.group(group).channel(NioSocketChannel.class).option(ChannelOption.TCP_NODELAY, true)
                .handler(new OutboundHandler(ctx.channel()));
        b.connect(targetHost, targetPort).sync();
    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
        super.exceptionCaught(ctx, cause);
    }

    @Override
    public void channelUnregistered(ChannelHandlerContext ctx) throws Exception {
        super.channelUnregistered(ctx);
    }

    private static class OutboundHandler extends ChannelInboundHandlerAdapter {
        private final Channel inboundChannel;

        OutboundHandler(Channel inboundChannel) {
            this.inboundChannel = inboundChannel;
        }


        @Override
        public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
            log.info("recv from:"+msg);
            inboundChannel.write(msg);
        }

        @Override
        public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
            closeOnFlush(inboundChannel);
        }

        @Override
        public void channelUnregistered(ChannelHandlerContext ctx) throws Exception {
            closeOnFlush(inboundChannel);
        }


    }

    static void closeOnFlush(Channel ch) {
        if (ch != null && ch.isRegistered()) {
            ch.closeFuture();
        }
    }
}
