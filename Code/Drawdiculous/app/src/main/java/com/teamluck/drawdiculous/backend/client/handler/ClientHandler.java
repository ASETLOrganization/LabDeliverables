package com.teamluck.drawdiculous.backend.client.handler;

import com.alibaba.fastjson.JSON;
import com.teamluck.drawdiculous.backend.Const;
import com.teamluck.drawdiculous.backend.protocol.GameProtocol;
import com.teamluck.drawdiculous.backend.protocol.Protocol;
import com.teamluck.drawdiculous.backend.protocol.RoomProtocol;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;

import java.util.logging.Logger;

/**
 * Client handler, receives all server packages and use
 * respective handlers to handle.
 */
public class ClientHandler extends SimpleChannelInboundHandler<String> {
    
    private static final Logger logger = Logger.getLogger(ClientHandler.class.getName());
    public static ClientRoomHandler clientRoomHandler;
    public static ClientGameHandler clientGameHandler;
    
    /**
     * Receives string data and parse string into Protocol object.
     * Calls respective handlers to handle the package based on protocol type
     */
    @Override
    protected void channelRead0(ChannelHandlerContext channelHandlerContext, String s) {
        assert clientRoomHandler != null;
        assert clientGameHandler != null;
        
        logger.info("Received message: " + s);
        logger.info("from: " + channelHandlerContext.channel().remoteAddress());
        
        // parse protocol from json string
        Protocol protocol = JSON.parseObject(s, Protocol.class);
        
        // handles server responses
        switch (protocol.getType()) {
            case Const.TYPE_ESTABLISH_CONNECTION: {
                logger.info("handling request type ESTABLISH_CONNECTION");
                // TODO: deal with this
                break;
            }
            case Const.TYPE_ROOM: {
                logger.info("handling request type ROOM");
                protocol = JSON.parseObject(s, RoomProtocol.class);
                clientRoomHandler.handle(protocol);
                break;
            }
            case Const.TYPE_GAME: {
                logger.info("handling request type DRAWING");
                protocol = JSON.parseObject(s, GameProtocol.class);
                clientGameHandler.handle(protocol);
                break;
            }
            default: {
                logger.info("handling request type WRONG");
            }
        }
    }
    
}
