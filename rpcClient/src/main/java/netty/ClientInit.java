package netty;

import io.netty.bootstrap.Bootstrap;
import io.netty.channel.*;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioSocketChannel;
import netty.handler.RequestEncoder;
import netty.handler.ResponseDecoder;
import netty.handler.ResponseProcessHandler;

import java.net.InetSocketAddress;
import java.net.SocketAddress;

public class ClientInit {
    private Bootstrap bootstrap = new Bootstrap();
    private EventLoopGroup eventLoopGroup = new NioEventLoopGroup();

    private Channel channel;

    public ClientInit(SocketAddress inetSocketAddress) {
        bootstrap.group(eventLoopGroup)
                .channel(NioSocketChannel.class)
                .option(ChannelOption.SO_KEEPALIVE, true)
                .option(ChannelOption.CONNECT_TIMEOUT_MILLIS, 5000)
                .option(ChannelOption.TCP_NODELAY,true)
                .handler(new ChannelInitializer<SocketChannel>() {
                    @Override
                    protected void initChannel(SocketChannel socketChannel) throws Exception {
                        socketChannel.pipeline().addLast(new RequestEncoder());
                        socketChannel.pipeline().addLast(new ResponseDecoder());
                        socketChannel.pipeline().addLast(new ResponseProcessHandler());
                    }
                });
        try{
            ChannelFuture channelFuture = bootstrap.connect(inetSocketAddress).sync();
            channel = channelFuture.channel();
        }catch(InterruptedException e){
            e.printStackTrace();
        }finally {
            eventLoopGroup.shutdownGracefully();
        }
    }
    public Channel getChannel(){
        return channel;
    }

}
