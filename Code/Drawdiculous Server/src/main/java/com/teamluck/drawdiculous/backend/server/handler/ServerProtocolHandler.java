package com.teamluck.drawdiculous.backend.server.handler;

import com.teamluck.drawdiculous.backend.protocol.Protocol;
import io.netty.channel.Channel;
import io.netty.channel.group.ChannelGroup;
import lombok.Data;

import java.util.logging.Logger;

/**
 * Abstract parent class for protocol handlers
 */
@Data
public abstract class ServerProtocolHandler {
    
    private static final Logger logger = Logger.getLogger(ServerProtocolHandler.class.getName());
    
    private Channel channel;
    
    public abstract void handle(Channel channel, Protocol protocol);
    
    void sendResponse(ChannelGroup channels, Channel ignore, String response) {
        logger.info("channels " + channels);
        for (Channel channel : channels) {
            if (channel != ignore) {
                logger.info("Sending to " + channel.remoteAddress() + ": " + response);
                channel.writeAndFlush(response + "\n");
            }
        }
    }
    
    void sendResponse(String response) {
        logger.info("Sending to " + channel.remoteAddress() + ": " + response);
        channel.writeAndFlush(response + "\n");
    }
    
    void sendResponse(Channel channel, String response) {
        logger.info("Sending to " + channel.remoteAddress() + ": " + response);
        channel.writeAndFlush(response + "\n");
    }
}
