package github.xunolan.rpcproject.netty;

import github.xunolan.rpcproject.enums.ExceptionEnum;
import github.xunolan.rpcproject.exception.RpcException;
import github.xunolan.rpcproject.extension.ExtensionLoader;
import github.xunolan.rpcproject.netty.handler.ResponseProcessHandler;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import io.netty.bootstrap.Bootstrap;
import io.netty.channel.*;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioSocketChannel;
import github.xunolan.rpcproject.netty.handler.codec.RequestEncoder;
import github.xunolan.rpcproject.netty.handler.codec.ResponseDecoder;
import github.xunolan.rpcproject.serializer.Serializer;
import github.xunolan.rpcproject.serializer.kryo.KryoSerializer;

import java.net.SocketAddress;
import java.util.ServiceLoader;

public class NettyClientInit {
    private final Logger log = LoggerFactory.getLogger(NettyClientInit.class);
    private final Bootstrap bootstrap = new Bootstrap();
    private final EventLoopGroup eventLoopGroup = new NioEventLoopGroup();
    private static Channel channel;
    private final SocketAddress socketAddress;
    private Serializer serializer;

    public NettyClientInit(SocketAddress socketAddress) {
       this.socketAddress = socketAddress;
        //init serializer
        serializer = (Serializer) ExtensionLoader.getExtensionLoader(Serializer.class).getExtension("kryo");
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
                        socketChannel.pipeline().addLast(new RequestEncoder(serializer));
                        socketChannel.pipeline().addLast(new ResponseDecoder(serializer));
                        socketChannel.pipeline().addLast(new ResponseProcessHandler());
                    }
                });
        try{
            ChannelFuture channelFuture = bootstrap.connect(socketAddress).sync();
            log.info("客户端已启动");
            channel = channelFuture.channel();
            channel.closeFuture().addListener(future -> {
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
