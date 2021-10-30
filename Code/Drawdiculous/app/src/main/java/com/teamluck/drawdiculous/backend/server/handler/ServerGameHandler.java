package com.teamluck.drawdiculous.backend.server.handler;

import com.alibaba.fastjson.JSON;
import com.teamluck.drawdiculous.backend.Const;
import com.teamluck.drawdiculous.backend.model.Room;
import com.teamluck.drawdiculous.backend.model.User;
import com.teamluck.drawdiculous.backend.protocol.GameProtocol;
import com.teamluck.drawdiculous.backend.protocol.Protocol;
import com.teamluck.drawdiculous.backend.server.utils.ServerMemory;
import io.netty.channel.Channel;

import java.util.logging.Logger;

/**
 * Handles game requests.
 */
public class ServerGameHandler extends ServerProtocolHandler {
    
    private static final Logger logger = Logger.getLogger(ServerGameHandler.class.getName());
    
    private GameProtocol protocol;
    
    /**
     * Handles game related requests according to the opcodes.
     */
    public void handle(Channel channel, Protocol protocol) {
        this.protocol = (GameProtocol) protocol;
        setChannel(channel);
        switch (protocol.getOpCode()) {
            case Const.OP_GAME_STROKE: {
                handleStrokeUpdate();
                break;
            }
            case Const.OP_GAME_GUESS: {
                handleGuessUpdate();
                break;
            }
            default:
                logger.info("invalid opCode");
        }
    }
    
    /**
     * Get response string for game related response.
     */
    private String getResponseString(Room room, int opCode, int status) {
        return JSON.toJSONString(getDefaultProtocol(room, opCode, status));
    }
    
    /**
     * Get response string with message for game related response.
     */
    private String getResponseString(Room room, int opCode, int status, String message) {
        GameProtocol gameProtocol = getDefaultProtocol(room, opCode, status);
        gameProtocol.setMessage(message);
        return JSON.toJSONString(gameProtocol);
    }
    
    /**
     * Get default game protocol. Template for game protocol.
     */
    private GameProtocol getDefaultProtocol(Room room, int opCode, int status) {
        GameProtocol gameProtocol = new GameProtocol();
        gameProtocol.setType(Const.TYPE_GAME);
        gameProtocol.setOpCode(opCode);
        gameProtocol.setStatus(status);
        gameProtocol.setSenderId(protocol.getSenderId());
        gameProtocol.setSenderName(protocol.getSenderName());
        gameProtocol.setTimeout(room.getTimeout());
        gameProtocol.setMessage(protocol.getMessage());
        gameProtocol.setWord(room.getWord());
        gameProtocol.setPainterId(room.getPainter().getId());
        gameProtocol.setPainterName(room.getPainter().getName());
        gameProtocol.setLeaderboard(room.getLeaderboard());
        gameProtocol.setStrokes(protocol.getStrokes());
        return gameProtocol;
    }
    
    /**
     * Handles drawing stroke update.
     * Send stroke update to all players except the drawer.
     */
    public void handleStrokeUpdate() {
        Room room = ServerMemory.getRoom(protocol.getRoomId());
        assert room != null;
        sendResponse(room.getChannels(), getChannel(), getResponseString(room, Const.OP_GAME_UPDATE_STROKE, Const.STATUS_OK));
    }
    
    /**
     * Handles guess updates when a player makes a guess.
     * Send guess update to all players.
     */
    public void handleGuessUpdate() {
        Room room = ServerMemory.getRoom(protocol.getRoomId());
        User user = ServerMemory.getUser(protocol.getSenderId());
        String guess = protocol.getMessage();
        assert room != null;
        assert user != null;
        assert guess != null;
        
        if (!room.isDrawing()) {
            sendResponse(room.getChannels(), null, getResponseString(room, Const.OP_GAME_UPDATE_GUESS, Const.STATUS_OK, guess));
            return;
        }
        
        if (user == room.getPainter()) {
            return;
        }
        
        if (guess.trim().equalsIgnoreCase(room.getWord())) {
            // correct answer should not be displayed
            guess = Const.MESSAGE_CORRECT_ANSWER;
            if (room.hasNotGuessed(user)) {
                room.updateLeaderboard(user);
            }
        }
        sendResponse(room.getChannels(), null, getResponseString(room, Const.OP_GAME_UPDATE_GUESS, Const.STATUS_OK, guess));
    }
}
