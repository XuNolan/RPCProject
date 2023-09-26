package netty;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import io.netty.bootstrap.Bootstrap;
import io.netty.channel.*;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioSocketChannel;
import lombok.extern.slf4j.Slf4j;
import netty.handler.RequestEncoder;
import netty.handler.ResponseDecoder;
import netty.handler.ResponseProcessHandler;

import java.net.InetSocketAddress;
import java.net.SocketAddress;
public class ClientInit {
    private Logger log = LoggerFactory.getLogger(ClientInit.class);
    private Bootstrap bootstrap = new Bootstrap();
    private EventLoopGroup eventLoopGroup = new NioEventLoopGroup();

    private static Channel channel;
    private SocketAddress socketAddress;

    public ClientInit(SocketAddress socketAddress) {
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
                    protected void initChannel(SocketChannel socketChannel) throws Exception {
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
            e.printStackTrace();
        }
    }
    public Channel getChannel(){
        return channel;
    }

}
