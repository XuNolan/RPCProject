package netty;

import com.sun.jdi.Bootstrap;
import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.*;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import io.netty.handler.logging.LoggingHandler;
import lombok.extern.slf4j.Slf4j;
import netty.handler.RequestDecoder;
import netty.handler.RequestProcessEndpoint;
import netty.handler.ResponseEncoder;

import java.net.InetSocketAddress;
import java.net.SocketAddress;

@Slf4j
public class ServerInit {
    private ServerBootstrap bootstrap = new ServerBootstrap();
    private EventLoopGroup workerGroup = new NioEventLoopGroup();
    private EventLoopGroup bossGroup = new NioEventLoopGroup();

    private Channel channel;
    private SocketAddress inetSocketAddress;

    public ServerInit(SocketAddress inetSocketAddress) {
        this.inetSocketAddress = (InetSocketAddress) inetSocketAddress;
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
                    protected void initChannel(SocketChannel socketChannel) throws Exception {
                        socketChannel.pipeline().addLast(new ResponseEncoder());
                        socketChannel.pipeline().addLast(new RequestDecoder());
                        socketChannel.pipeline().addLast(new RequestProcessEndpoint(channel));
                    }
                });
        try{
            ChannelFuture channelFuture = bootstrap.bind(inetSocketAddress).sync();
            channel = channelFuture.channel();
            channel.closeFuture().addListener(
                    (ChannelFutureListener) future -> {
                        log.info("server已退出");
                        bossGroup.shutdownGracefully();
                        workerGroup.shutdownGracefully();
                    }
            );
            log.info("server已启动");
        }catch(InterruptedException e){
            e.printStackTrace();
        }
    }
    public Channel getChannel(){
        return channel;
    }
}
