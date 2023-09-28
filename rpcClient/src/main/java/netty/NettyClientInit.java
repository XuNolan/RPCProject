package netty;

import enums.ExceptionEnum;
import exception.RpcException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import io.netty.bootstrap.Bootstrap;
import io.netty.channel.*;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioSocketChannel;
import netty.handler.codec.RequestEncoder;
import netty.handler.codec.ResponseDecoder;
import netty.handler.ResponseProcessHandler;

import java.net.SocketAddress;
public class NettyClientInit {
    private final Logger log = LoggerFactory.getLogger(NettyClientInit.class);
    private final Bootstrap bootstrap = new Bootstrap();
    private final EventLoopGroup eventLoopGroup = new NioEventLoopGroup();

    private static Channel channel;
    private final SocketAddress socketAddress;

    public NettyClientInit(SocketAddress socketAddress) {
       this.socketAddress = socketAddress;
    }
    public void run(){
        bootstrap.group(eventLoopGroup)
                .channel(NioSocketChannel.class)
                .option(ChannelOption.SO_KEEPALIVE, true)
                .option(ChannelOption.CONNECT_TIMEOUT_MILLIS, 5000)
                .option(ChannelOption.TCP_NODELAY,true)
                .handler(new ChannelInitializer<SocketChannel>() {
                    @Override
                    protected void initChannel(SocketChannel socketChannel) {
                        socketChannel.pipeline().addLast(new RequestEncoder());
                        socketChannel.pipeline().addLast(new ResponseDecoder());
                        socketChannel.pipeline().addLast(new ResponseProcessHandler());
                    }
                });
        try{
            ChannelFuture channelFuture = bootstrap.connect(socketAddress).sync();
            log.info("客户端已启动");
            channel = channelFuture.channel();
            channel.closeFuture().addListener( future -> {
                log.info("服务端已退出");
                eventLoopGroup.shutdownGracefully();
            });
        }catch(InterruptedException e){
            throw new RpcException(ExceptionEnum.RpcClientInitFail, e);
        }
    }
    public Channel getChannel(){
        return channel;
    }

}
