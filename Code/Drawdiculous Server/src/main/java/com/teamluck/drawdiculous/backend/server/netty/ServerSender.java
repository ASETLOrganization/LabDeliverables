package com.teamluck.drawdiculous.backend.server.netty;

import com.alibaba.fastjson.JSON;
import com.teamluck.drawdiculous.backend.Const;
import com.teamluck.drawdiculous.backend.model.Room;
import com.teamluck.drawdiculous.backend.protocol.GameProtocol;
import io.netty.channel.Channel;
import io.netty.channel.group.ChannelGroup;
import lombok.Data;

import java.util.logging.Logger;

/**
 * Sending Game protocols.
 */
@Data
public class ServerSender {
    
    private static final Logger logger = Logger.getLogger(ServerSender.class.getName());
    
    /**
     * Sends response with ignore and response string to clients
     *
     * @param channels Specifies client channels to send response to.
     * @param ignore   Specifies channels to ignore when sending the responses.
     * @param response Response to be sent. In string.
     */
    public static void sendResponse(ChannelGroup channels, Channel ignore, String response) {
        for (Channel channel : channels) {
            if (channel != ignore) {
                logger.info("Sending to " + channel.remoteAddress() + ": " + response);
                channel.writeAndFlush(response + "\n");
            }
        }
    }
    
    /**
     * Send response to clients.
     */
    public static void sendResponse(String response, Channel channel) {
        logger.info("Sending to " + channel.remoteAddress() + ": " + response);
        channel.writeAndFlush(response + "\n");
    }
    
    /**
     * Create default game protocol.
     * Game protocol template.
     */
    private static GameProtocol getDefaultProtocol(Room room, int opCode, int status) {
        GameProtocol gameProtocol = new GameProtocol();
        gameProtocol.setType(Const.TYPE_GAME);
        gameProtocol.setOpCode(opCode);
        gameProtocol.setStatus(status);
        gameProtocol.setSenderId(Const.SERVER_ID);
        gameProtocol.setSenderName(Const.SERVER_NAME);
        gameProtocol.setTimeout(room.getTimeout());
        gameProtocol.setMessage("");
        gameProtocol.setWord("");
        gameProtocol.setPainterId(room.getPainter().getId());
        gameProtocol.setPainterName(room.getPainter().getName());
        gameProtocol.setLeaderboard(room.getLeaderboard());
        gameProtocol.setStrokes(null);
        return gameProtocol;
    }
    
    /**
     * Create response and send timeout response to a room.
     */
    public static void updateTimeout(Room room) {
        GameProtocol gameProtocol = getDefaultProtocol(room, Const.OP_GAME_UPDATE_TIMEOUT, Const.STATUS_OK);
        sendResponse(room.getChannels(), null, JSON.toJSONString(gameProtocol));
    }
    
    public static void updateStartRound(Room room) {
        GameProtocol gameProtocol = getDefaultProtocol(room, Const.OP_GAME_UPDATE_START_ROUND, Const.STATUS_OK);
        gameProtocol.setWord(room.getWord());
        sendResponse(JSON.toJSONString(gameProtocol), room.getPainter().getChannel());
        StringBuilder word = new StringBuilder();
        for (int i = 0; i < room.getWord().length(); i++) {
            word.append("_ ");
        }
        gameProtocol.setWord(word.toString());
        sendResponse(room.getChannels(), room.getPainter().getChannel(), JSON.toJSONString(gameProtocol));
    }
    
    public static void updateStartDraw(Room room) {
        GameProtocol gameProtocol = getDefaultProtocol(room, Const.OP_GAME_UPDATE_START_DRAW, Const.STATUS_OK);
        sendResponse(room.getChannels(), null, JSON.toJSONString(gameProtocol));
    }
    
    public static void updateGameFinish(Room room) {
        GameProtocol gameProtocol = getDefaultProtocol(room, Const.OP_GAME_UPDATE_GAME_FINISH, Const.STATUS_OK);
        sendResponse(room.getChannels(), null, JSON.toJSONString(gameProtocol));
    }
}
