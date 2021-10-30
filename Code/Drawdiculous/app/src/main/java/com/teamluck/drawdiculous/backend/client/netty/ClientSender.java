package com.teamluck.drawdiculous.backend.client.netty;

import com.alibaba.fastjson.JSON;
import com.teamluck.drawdiculous.backend.Const;
import com.teamluck.drawdiculous.backend.client.utils.ClientMemory;
import com.teamluck.drawdiculous.backend.model.DrawAction;
import com.teamluck.drawdiculous.backend.protocol.GameProtocol;
import com.teamluck.drawdiculous.backend.protocol.Protocol;
import com.teamluck.drawdiculous.backend.protocol.RoomProtocol;
import io.netty.channel.Channel;

import java.util.ArrayList;

/**
 * Client sender, create and send request to server.
 */
public class ClientSender {
    
    public static Channel channel;
    
    public static void send(String message) {
        channel.writeAndFlush(message + "\n");
    }
    
    public static void send(Protocol protocol) {
        send(JSON.toJSONString(protocol));
    }
    
    /**
     * Create and send request to server to request connection.
     */
    public static void requestEstablishConnection() {
        Protocol request = new Protocol();
        request.setType(Const.TYPE_ESTABLISH_CONNECTION);
        request.setOpCode(Const.OP_ESTABLISH_REQUEST);
        request.setRoomId(Const.INT_NULL_VALUE);
        request.setStatus(Const.STATUS_OK);
        request.setSenderId(ClientMemory.userId);
        request.setSenderName(ClientMemory.userName);
        send(request);
    }
    
    /**
     * Get default room protocol, room protocol template.
     */
    private static RoomProtocol getDefaultRoomProtocol() {
        RoomProtocol request = new RoomProtocol();
        request.setType(Const.TYPE_ROOM);
        // OpCode is not defaulted
        request.setRoomId(Const.INT_NULL_VALUE);
        request.setStatus(Const.INT_NULL_VALUE);
        request.setSenderId(ClientMemory.userId);
        request.setSenderName(ClientMemory.userName);
        
        request.setKickPlayerId(Const.INT_NULL_VALUE);
        request.setRoomStatus(Const.INT_NULL_VALUE);
        request.setPassword("");
        request.setUsers(null);
        return request;
    }
    
    /**
     * Create and send request for creating new room.
     */
    public static void requestCreateRoom(String password) {
        RoomProtocol request = getDefaultRoomProtocol();
        request.setOpCode(Const.OP_ROOM_CREATE);
        request.setPassword(password);
        send(request);
    }
    
    /**
     * Create and send request for deleting room.
     */
    public static void requestDeleteRoom() {
        RoomProtocol request = getDefaultRoomProtocol();
        request.setOpCode(Const.OP_ROOM_DELETE);
        request.setRoomId(ClientMemory.roomId);
        send(request);
    }
    
    /**
     * Create and send request to join room.
     */
    public static void requestJoinRoom(int roomId, String password) {
        RoomProtocol request = getDefaultRoomProtocol();
        request.setOpCode(Const.OP_ROOM_JOIN);
        request.setRoomId(roomId);
        request.setPassword(password);
        send(request);
    }
    
    /**
     * Create request to join public room (no password).
     */
    public static void requestJoinRoom(int roomId) {
        requestJoinRoom(roomId, "");
    }
    
    /**
     * Create and send request to join random room.
     */
    public static void requestJoinRandomRoom() {
        RoomProtocol request = getDefaultRoomProtocol();
        request.setOpCode(Const.OP_ROOM_RANDOM_JOIN);
        send(request);
    }
    
    /**
     * Create and send request to leave room.
     */
    public static void requestLeaveRoom() {
        RoomProtocol request = getDefaultRoomProtocol();
        request.setOpCode(Const.OP_ROOM_LEAVE);
        request.setRoomId(ClientMemory.roomId);
        send(request);
    }
    
    /**
     * Create and send request by room host to remove a user from room.
     */
    public static void requestDeleteUser(int kickPlayerId) {
        RoomProtocol request = getDefaultRoomProtocol();
        request.setOpCode(Const.OP_ROOM_DELETE_USER);
        request.setRoomId(ClientMemory.roomId);
        request.setKickPlayerId(kickPlayerId);
        send(request);
    }
    
    /**
     * Create and send request by room host to start game.
     */
    public static void requestStartGame() {
        RoomProtocol request = getDefaultRoomProtocol();
        request.setOpCode(Const.OP_ROOM_START_GAME);
        request.setRoomId(ClientMemory.roomId);
        send(request);
    }
    
    /**
     * Get default room protocol, room protocol template.
     */
    private static GameProtocol getDefaultGameProtocol() {
        GameProtocol request = new GameProtocol();
        request.setType(Const.TYPE_GAME);
        // OpCode is not defaulted
        request.setRoomId(Const.INT_NULL_VALUE);
        request.setStatus(Const.INT_NULL_VALUE);
        request.setSenderId(ClientMemory.userId);
        request.setSenderName(ClientMemory.userName);
        
        request.setTimeout(Const.INT_NULL_VALUE);
        request.setMessage("");
        request.setWord("");
        request.setPainterName("");
        request.setPainterId(Const.INT_NULL_VALUE);
        request.setLeaderboard(null);
        request.setStrokes(null);
        
        return request;
    }
    
    /**
     * Create and send request for drawing stroke update.
     */
    public static void requestUpdateStrokes(ArrayList<DrawAction> strokes) {
        GameProtocol request = getDefaultGameProtocol();
        request.setOpCode(Const.OP_GAME_STROKE);
        request.setRoomId(ClientMemory.roomId);
        request.setStrokes(strokes);
        send(request);
    }
    
    /**
     * Create and send request when a player makes a guess.
     */
    public static void requestSendGuess(String guess) {
        GameProtocol request = getDefaultGameProtocol();
        request.setOpCode(Const.OP_GAME_GUESS);
        request.setRoomId(ClientMemory.roomId);
        request.setMessage(guess);
        send(request);
    }
    
}
