package com.teamluck.drawdiculous.backend.client.handler;

import com.teamluck.drawdiculous.backend.Const;
import com.teamluck.drawdiculous.backend.protocol.Protocol;
import com.teamluck.drawdiculous.backend.protocol.RoomProtocol;

import java.util.logging.Logger;

/**
 * Client protocol handler, handles server packages regarding updates about room.
 * Extended by AppRoomHandler
 */
public abstract class ClientRoomHandler extends ClientProtocolHandler {
    
    private static final Logger logger = Logger.getLogger(ClientRoomHandler.class.getName());
    
    protected RoomProtocol protocol;
    
    /**
     * Handles different type of room updates by calling different functions
     * based on the protocol opcode.
     */
    @Override
    public void handle(Protocol protocol) {
        this.protocol = (RoomProtocol) protocol;
        switch (protocol.getOpCode()) {
            case (Const.OP_ROOM_RESPONSE_CREATE): {
                handleCreate();
                break;
            }
            case (Const.OP_ROOM_RESPONSE_JOIN): {
                handleJoin();
                break;
            }
            case (Const.OP_ROOM_RESPONSE_UPDATE): {
                handleUpdate();
                break;
            }
            case (Const.OP_ROOM_RESPONSE_LEAVE): {
                handleLeave();
                break;
            }
            case (Const.OP_ROOM_RESPONSE_START_GAME): {
                handleStartGame();
                break;
            }
            default: {
                logger.info("invalid opcode");
            }
        }
    }
    
    /**
     * Handles client request when a new room is created.
     */
    public abstract void handleCreate();
    
    /**
     * Handles client request when a new player joins the room.
     */
    public abstract void handleJoin();
    
    /**
     * Updates players in room when room state changes.
     */
    public abstract void handleUpdate();
    
    /**
     * Handles client request when a player leaves the room.
     */
    public abstract void handleLeave();
    
    /**
     * Handles client request when the room host starts the game.
     */
    public abstract void handleStartGame();
    
}
