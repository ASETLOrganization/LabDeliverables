package com.teamluck.drawdiculous.backend.client.netty;

import com.teamluck.drawdiculous.backend.Const;
import io.netty.bootstrap.Bootstrap;
import io.netty.channel.ChannelOption;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.nio.NioSocketChannel;

import java.util.logging.Logger;

/**
 * Client, runs the client.
 */
public class Client {
    
    private static final Logger logger = Logger.getLogger(Client.class.getName());
    private static EventLoopGroup eventLoopGroup;
    
    /**
     * Runs client and connect client to server.
     */
    public static void run() throws InterruptedException {
        eventLoopGroup = new NioEventLoopGroup();
        
        Bootstrap bootstrap = new Bootstrap()
                .group(eventLoopGroup)
                .channel(NioSocketChannel.class)
                .option(ChannelOption.SO_KEEPALIVE, true)
                .handler(new ClientInitializer());
        
        ClientSender.channel = bootstrap.connect(Const.HOST, Const.PORT).sync().channel();
    }
    
    /**
     * Ends connection with server.
     */
    public static void endConnection() {
        if (eventLoopGroup != null) {
            eventLoopGroup.shutdownGracefully();
        }
    }
    
}
