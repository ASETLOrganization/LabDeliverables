package com.teamluck.drawdiculous.backend.server.handler;

import com.alibaba.fastjson.JSON;
import com.teamluck.drawdiculous.backend.Const;
import com.teamluck.drawdiculous.backend.model.Room;
import com.teamluck.drawdiculous.backend.model.User;
import com.teamluck.drawdiculous.backend.protocol.GameProtocol;
import com.teamluck.drawdiculous.backend.protocol.Protocol;
import com.teamluck.drawdiculous.backend.protocol.RoomProtocol;
import com.teamluck.drawdiculous.backend.server.utils.ServerMemory;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;

import java.util.logging.Logger;

/**
 *
 */
public class ServerHandler extends SimpleChannelInboundHandler<String> {
    
    private static final Logger logger = Logger.getLogger(ServerHandler.class.getName());
    
    // TODO: deal with handler add and remove
    
    @Override
    public void handlerAdded(ChannelHandlerContext ctx) throws Exception {
        super.handlerAdded(ctx);
        logger.info("Added handler: " + ctx.channel().remoteAddress());
    }
    
    @Override
    public void handlerRemoved(ChannelHandlerContext ctx) throws Exception {
        super.handlerRemoved(ctx);
        User user = ServerMemory.getUser(ctx.channel());
        Room room = ServerMemory.getUserRoom(user);
        if (user == null) {
            return;
        }
        if (room != null) {
            if (user.getId() == room.getHost().getId()) {
                new ServerRoomHandler().roomDelete(room, user);
            }
            else {
                new ServerRoomHandler().roomLeave(room, user);
            }
        }
        
        ServerMemory.USERS.remove(user);
        logger.info("Removed handler: " + ctx.channel().remoteAddress());
    }
    
    @Override
    protected void channelRead0(ChannelHandlerContext channelHandlerContext, String s) {
        logger.info("Received message: " + s);
        logger.info("from: " + channelHandlerContext.channel().remoteAddress());
        
        // parse protocol from json string
        Protocol protocol = JSON.parseObject(s, Protocol.class);
        
        // handles requests
        switch (protocol.getType()) {
            // TODO: encapsulation
            // TODO: ask for id if not registered
            case Const.TYPE_ESTABLISH_CONNECTION: {
                logger.info("handling request type ESTABLISH_CONNECTION");
                User user = new User();
                user.setId(protocol.getSenderId());
                user.setName(protocol.getSenderName());
                user.setChannel(channelHandlerContext.channel());
                ServerMemory.USERS.add(user);
                logger.info("added user");
                logger.info(ServerMemory.USERS.toString());
                
                Protocol response = new Protocol();
                response.setType(Const.TYPE_ESTABLISH_CONNECTION);
                response.setOpCode(Const.OP_ESTABLISH_RESPONSE);
                response.setRoomId(Const.INT_NULL_VALUE);
                response.setStatus(Const.STATUS_OK);
                response.setSenderId(protocol.getSenderId());
                response.setSenderName(protocol.getSenderName());
                
                channelHandlerContext.channel().writeAndFlush(JSON.toJSONString(response) + "\n");
                break;
            }
            case Const.TYPE_ROOM: {
                logger.info("handling request type ROOM");
                protocol = JSON.parseObject(s, RoomProtocol.class);
                ServerRoomHandler serverRoomHandler = new ServerRoomHandler();
                serverRoomHandler.handle(channelHandlerContext.channel(), protocol);
                break;
            }
            case Const.TYPE_GAME: {
                logger.info("handling request type DRAWING");
                protocol = JSON.parseObject(s, GameProtocol.class);
                ServerGameHandler serverGameHandler = new ServerGameHandler();
                serverGameHandler.handle(channelHandlerContext.channel(), protocol);
                break;
            }
            default: {
                logger.info("handling request type WRONG");
            }
        }
    }
    
}
