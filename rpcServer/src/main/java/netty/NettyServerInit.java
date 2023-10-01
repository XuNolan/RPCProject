package netty;

import enums.ExceptionEnum;
import exception.RpcException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.*;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import io.netty.handler.logging.LoggingHandler;
import netty.handler.codec.RequestDecoder;
import netty.handler.RequestProcessEndpoint;
import netty.handler.codec.ResponseEncoder;
import serializer.Serializer;
import serializer.kryo.KryoSerializer;

import java.net.SocketAddress;


public class NettyServerInit {
    private static final Logger log = LoggerFactory.getLogger(NettyServerInit.class);
    private final ServerBootstrap bootstrap = new ServerBootstrap();
    private final EventLoopGroup workerGroup = new NioEventLoopGroup();
    private final EventLoopGroup bossGroup = new NioEventLoopGroup();

    private Channel channel;
    private final SocketAddress inetSocketAddress;
    private final Serializer serializer = new KryoSerializer();

    public NettyServerInit(SocketAddress inetSocketAddress) {
        this.inetSocketAddress = inetSocketAddress;
    }
    public void run(){
        bootstrap.group(bossGroup, workerGroup)
                .handler(new LoggingHandler())
                .channel(NioServerSocketChannel.class)
                .childOption(ChannelOption.TCP_NODELAY, Boolean.TRUE)
                .childOption(ChannelOption.SO_KEEPALIVE, Boolean.TRUE)
                .option(ChannelOption.SO_BACKLOG, 128)
                .childHandler(new ChannelInitializer<SocketChannel>() {
                    @Override
                    protected void initChannel(SocketChannel socketChannel) {
                        socketChannel.pipeline().addLast(new ResponseEncoder(serializer));
                        socketChannel.pipeline().addLast(new RequestDecoder(serializer));
                        socketChannel.pipeline().addLast(new RequestProcessEndpoint());
                    }
                });
        try{
            ChannelFuture channelFuture = bootstrap.bind(inetSocketAddress).sync();
            this.channel = channelFuture.channel();
            this.channel.closeFuture().addListener(
                    (ChannelFutureListener) future -> {
                        log.info("server已退出");
                        bossGroup.shutdownGracefully();
                        workerGroup.shutdownGracefully();
                    }
            );
            log.info("server已启动");
        }catch(InterruptedException e){
            throw new RpcException(ExceptionEnum.RpcServerInitFail, e);
        }
    }
}
