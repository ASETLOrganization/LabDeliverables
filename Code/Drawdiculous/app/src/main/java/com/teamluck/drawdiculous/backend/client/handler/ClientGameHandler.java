package com.teamluck.drawdiculous.backend.client.handler;

import com.teamluck.drawdiculous.backend.Const;
import com.teamluck.drawdiculous.backend.protocol.GameProtocol;
import com.teamluck.drawdiculous.backend.protocol.Protocol;

import java.util.logging.Logger;

/**
 * Client game handler, handles server packages regarding updates about game.
 * Extended by AppGameHandler
 */
public abstract class ClientGameHandler extends ClientProtocolHandler {
    
    private static final Logger logger = Logger.getLogger(ClientGameHandler.class.getName());
    
    protected GameProtocol protocol;
    
    /**
     * Handles different type of game updates by calling different functions
     * based on the protocol opcode.
     */
    @Override
    public void handle(Protocol protocol) {
        this.protocol = (GameProtocol) protocol;
        switch (protocol.getOpCode()) {
            case (Const.OP_GAME_UPDATE_STROKE): {
                handleStrokeUpdate();
                break;
            }
            case (Const.OP_GAME_UPDATE_GUESS): {
                handleGuessUpdate();
                break;
            }
            case (Const.OP_GAME_UPDATE_STATUS): {
                handleStatusUpdate();
                break;
            }
            case (Const.OP_GAME_UPDATE_TIMEOUT): {
                handleTimeout();
                break;
            }
            case (Const.OP_GAME_UPDATE_START_ROUND): {
                handleStartRound();
                break;
            }
            case (Const.OP_GAME_UPDATE_START_DRAW): {
                handleStartDraw();
                break;
            }
            case (Const.OP_GAME_UPDATE_GAME_FINISH): {
                handleGameFinish();
                break;
            }
            default: {
                logger.info("invalid opcode");
            }
        }
    }
    
    /**
     * Handles updates when drawer draws
     */
    public abstract void handleStrokeUpdate();
    
    /**
     * Handles updates when a player makes a guess
     */
    public abstract void handleGuessUpdate();
    
    /**
     *
     */
    public abstract void handleStatusUpdate();
    
    /**
     * Handles updates when a game timeouts
     */
    public abstract void handleTimeout();
    
    public abstract void handleStartRound();
    
    public abstract void handleStartDraw();
    
    public abstract void handleGameFinish();
    
}
