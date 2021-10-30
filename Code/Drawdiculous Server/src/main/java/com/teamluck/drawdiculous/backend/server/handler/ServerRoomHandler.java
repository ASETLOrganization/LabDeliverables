package com.teamluck.drawdiculous.backend.server.handler;

import com.alibaba.fastjson.JSON;
import com.teamluck.drawdiculous.backend.Const;
import com.teamluck.drawdiculous.backend.model.Room;
import com.teamluck.drawdiculous.backend.model.User;
import com.teamluck.drawdiculous.backend.protocol.Protocol;
import com.teamluck.drawdiculous.backend.protocol.RoomProtocol;
import com.teamluck.drawdiculous.backend.server.utils.ServerMemory;
import com.teamluck.drawdiculous.backend.server.utils.ServerUtils;
import io.netty.channel.Channel;

import java.util.logging.Logger;

/**
 * Handles room requests
 */
public class ServerRoomHandler extends ServerProtocolHandler {
    
    private static final Logger logger = Logger.getLogger(ServerRoomHandler.class.getName());
    
    private RoomProtocol protocol;
    
    @Override
    public void handle(Channel channel, Protocol protocol) {
        this.protocol = (RoomProtocol) protocol;
        setChannel(channel);
        switch (protocol.getOpCode()) {
            case (Const.OP_ROOM_CREATE):
                roomCreate();
                break;
            case (Const.OP_ROOM_DELETE):
                roomDelete();
                break;
            case (Const.OP_ROOM_JOIN):
                roomJoin();
                break;
            case (Const.OP_ROOM_RANDOM_JOIN):
                roomRandomJoin();
                break;
            case (Const.OP_ROOM_LEAVE):
                roomLeave();
                break;
            case (Const.OP_ROOM_DELETE_USER):
                roomDeleteUser();
                break;
            case (Const.OP_ROOM_START_GAME):
                startGame();
                break;
            case (Const.OP_ROOM_START_ROUND):
                startRound();
                break;
            default:
                logger.info("invalid opCode");
        }
    }
    
    private String getResponseString(User user, Room room, int opCode, int status, int kickPlayer) {
        RoomProtocol roomProtocol = new RoomProtocol();
        roomProtocol.setType(Const.TYPE_ROOM);
        roomProtocol.setOpCode(opCode);
        roomProtocol.setStatus(status);
        roomProtocol.setSenderId(user.getId());
        roomProtocol.setSenderName(user.getName());
        if (room != null) {
            roomProtocol.setRoomId(room.getId());
            roomProtocol.setRoomStatus(room.getStatus());
            roomProtocol.setUsers(room.getUserNames());
        }
        roomProtocol.setKickPlayerId(kickPlayer);
        
        return JSON.toJSONString(roomProtocol);
    }
    
    private String getResponseString(User user, Room room, int opCode, int status) {
        return getResponseString(user, room, opCode, status, Const.INT_NULL_VALUE);
    }
    
    private void roomCreate() {
        User user = ServerMemory.getUser(protocol.getSenderId());
        assert user != null;
        
        Room room = new Room();
        room.setId(ServerUtils.generateRoomId());
        room.setHost(user);
        room.addUser(user);
        room.setPassword(protocol.getPassword());
        room.setStatus(Const.ROOM_STATUS_WAITING);
        
        logger.info("Created room:" + room.getId());
        
        ServerMemory.addRoom(room);
        
        sendResponse(getResponseString(user, room, Const.OP_ROOM_RESPONSE_CREATE, Const.STATUS_OK));
    }
    
    private void roomDelete() {
        User user = ServerMemory.getUser(protocol.getSenderId());
        Room room = ServerMemory.getRoom(protocol.getRoomId());
        
        if (room == null) {
            sendResponse(getResponseString(user, null, Const.OP_ROOM_RESPONSE_LEAVE, Const.STATUS_NOT_FOUND));
            return;
        }
        
        // not allowed if the user is not the host
        if (room.getHost() != user) {
            sendResponse(getResponseString(user, room, Const.OP_ROOM_RESPONSE_LEAVE, Const.STATUS_FORBIDDEN));
            return;
        }
        
        roomDelete(room, user);
    }
    
    public void roomDelete(Room room, User user) {
        assert room != null;
        assert user != null;
        room.setStatus(Const.ROOM_STATUS_CLOSED);
        sendResponse(room.getChannels(), null, getResponseString(user, room, Const.OP_ROOM_RESPONSE_LEAVE, Const.STATUS_OK));
        
        ServerMemory.removeRoom(room);
    }
    
    private void roomJoin() {
        User user = ServerMemory.getUser(protocol.getSenderId());
        Room room = ServerMemory.getRoom(protocol.getRoomId());
        if (room == null) {
            sendResponse(getResponseString(user, null, Const.OP_ROOM_RESPONSE_JOIN, Const.STATUS_NOT_FOUND));
            return;
        }
        
        // check password for private room
        if (room.isPrivate()) {
            if (room.wrongPassword(protocol.getPassword())) {
                sendResponse(getResponseString(user, room, Const.OP_ROOM_RESPONSE_JOIN, Const.STATUS_FORBIDDEN));
                return;
            }
        }
        else {
            if (protocol.getPassword().length() != 0) {
                sendResponse(getResponseString(user, room, Const.OP_ROOM_RESPONSE_JOIN, Const.STATUS_NOT_FOUND));
                return;
            }
        }
        
        // check if the room is available for joining
        if (room.full() || room.getStatus() != Const.ROOM_STATUS_WAITING) {
            sendResponse(getResponseString(user, room, Const.OP_ROOM_RESPONSE_JOIN, Const.STATUS_FORBIDDEN));
            return;
        }
        
        // check if user is already in room
        if (room.contains(user)) {
            sendResponse(getResponseString(user, room, Const.OP_ROOM_RESPONSE_JOIN, Const.STATUS_FORBIDDEN));
            return;
        }
        
        assert user != null;
        room.addUser(user);
        sendResponse(getResponseString(user, room, Const.OP_ROOM_RESPONSE_JOIN, Const.STATUS_OK));
        sendResponse(room.getChannels(), user.getChannel(), getResponseString(user, room, Const.OP_ROOM_RESPONSE_UPDATE, Const.STATUS_OK));
    }
    
    private void roomRandomJoin() {
        User user = ServerMemory.getUser(protocol.getSenderId());
        Room room = ServerMemory.getRandomPublicRoom();
        assert user != null;
        
        // no available public rooms
        if (room == null) {
            sendResponse(getResponseString(user, null, Const.OP_ROOM_RESPONSE_JOIN, Const.STATUS_NOT_FOUND));
            return;
        }
        room.addUser(user);
        sendResponse(getResponseString(user, room, Const.OP_ROOM_RESPONSE_JOIN, Const.STATUS_OK));
        sendResponse(room.getChannels(), user.getChannel(), getResponseString(user, room, Const.OP_ROOM_RESPONSE_UPDATE, Const.STATUS_OK));
    }
    
    private void roomLeave() {
        User user = ServerMemory.getUser(protocol.getSenderId());
        Room room = ServerMemory.getRoom(protocol.getRoomId());
        
        if (room == null) {
            sendResponse(getResponseString(user, null, Const.OP_ROOM_RESPONSE_LEAVE, Const.STATUS_NOT_FOUND));
            return;
        }
        
        if (room.notContains(user)) {
            sendResponse(getResponseString(user, room, Const.OP_ROOM_RESPONSE_LEAVE, Const.STATUS_FORBIDDEN));
            return;
        }
        
        roomLeave(room, user);
    }
    
    public void roomLeave(Room room, User user) {
        assert user != null;
        assert room != null;
        room.removeUser(user);
        
        sendResponse(room.getChannels(), null, getResponseString(user, room, Const.OP_ROOM_RESPONSE_UPDATE, Const.STATUS_OK));
        sendResponse(getResponseString(user, room, Const.OP_ROOM_RESPONSE_LEAVE, Const.STATUS_OK));
    }
    
    private void roomDeleteUser() {
        User user = ServerMemory.getUser(protocol.getSenderId());
        Room room = ServerMemory.getRoom(protocol.getRoomId());
        User kickedUser = ServerMemory.getUser(protocol.getKickPlayerId());
        
        if (room == null) {
            sendResponse(getResponseString(user, null, Const.OP_ROOM_RESPONSE_UPDATE, Const.STATUS_NOT_FOUND));
            return;
        }
        
        if (room.notContains(kickedUser) || room.notContains(user)) {
            sendResponse(getResponseString(user, room, Const.OP_ROOM_RESPONSE_UPDATE, Const.STATUS_NOT_FOUND));
            return;
        }
        
        // check if sender is host of room
        if (protocol.getSenderId() != room.getHost().getId()) {
            sendResponse(getResponseString(user, room, Const.OP_ROOM_RESPONSE_UPDATE, Const.STATUS_FORBIDDEN));
            return;
        }
        
        logger.info("Kicking player: " + kickedUser);
        
        assert kickedUser != null;
        room.removeUser(kickedUser);
        sendResponse(room.getChannels(), null, getResponseString(user, room, Const.OP_ROOM_RESPONSE_UPDATE, Const.STATUS_OK));
        sendResponse(kickedUser.getChannel(), getResponseString(kickedUser, room, Const.OP_ROOM_RESPONSE_LEAVE, Const.STATUS_OK));
    }
    
    private void startGame() {
        User user = ServerMemory.getUser(protocol.getSenderId());
        Room room = ServerMemory.getRoom(protocol.getRoomId());
        
        if (room == null) {
            sendResponse(getResponseString(user, null, Const.OP_ROOM_RESPONSE_START_GAME, Const.STATUS_NOT_FOUND));
            return;
        }
        
        if (room.getStatus() != Const.ROOM_STATUS_WAITING) {
            sendResponse(getResponseString(user, null, Const.OP_ROOM_RESPONSE_START_GAME, Const.STATUS_FORBIDDEN));
            return;
        }
        
        // not allowed if the user is not the host
        if (room.getHost() != user) {
            sendResponse(getResponseString(user, room, Const.OP_ROOM_RESPONSE_START_GAME, Const.STATUS_FORBIDDEN));
            return;
        }
        
        sendResponse(room.getChannels(), null, getResponseString(user, room, Const.OP_ROOM_RESPONSE_START_GAME, Const.STATUS_OK));
        room.startGame();
    }
    
    private void startRound() {
        Room room = ServerMemory.getRoom(protocol.getRoomId());
        assert room != null;
    }
    
}
