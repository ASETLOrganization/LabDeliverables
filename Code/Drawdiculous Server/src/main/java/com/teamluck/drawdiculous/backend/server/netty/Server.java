package com.teamluck.drawdiculous.backend.server.netty;

import com.teamluck.drawdiculous.backend.Const;
import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.nio.NioServerSocketChannel;

import java.util.logging.Logger;

/**
 * Server main
 */
public class Server {
    
    private static final Logger logger = Logger.getLogger(Server.class.getName());
    private final int port;
    
    /**
     * Server constructor
     */
    public Server(int serverPort) {
        port = serverPort;
    }
    
    /**
     * main function for server,
     * Starts the server
     */
    public static void main(String[] args) {
        new Server(Const.PORT).run();
    }
    
    /**
     * Runs the server
     */
    public void run() {
        logger.info("starting server");
        EventLoopGroup bossGroup = new NioEventLoopGroup();
        EventLoopGroup workerGroup = new NioEventLoopGroup();
        
        try {
            ServerBootstrap bootstrap = new ServerBootstrap()
                    .group(bossGroup, workerGroup)
                    .channel(NioServerSocketChannel.class)
                    .childHandler(new ServerInitializer());
            
            bootstrap.bind(port).sync().channel().closeFuture().sync();
        }
        catch (InterruptedException e) {
            e.printStackTrace();
        }
        finally {
            bossGroup.shutdownGracefully();
            workerGroup.shutdownGracefully();
            logger.info("shutting down server");
        }
    }
    
}
