package com.teamluck.drawdiculous.app.handler;

import static com.teamluck.drawdiculous.app.handler.AppBufferHandler.handleBufferStartRound;

import android.content.Intent;

import com.teamluck.drawdiculous.app.activity.ResultActivity;
import com.teamluck.drawdiculous.app.utils.AppMem;
import com.teamluck.drawdiculous.backend.Const;
import com.teamluck.drawdiculous.backend.client.handler.ClientGameHandler;

/**
 * App game handler, handles server packages about updates in the game.
 */
public class AppGameHandler extends ClientGameHandler {
    
    private static final String TAG = AppGameHandler.class.getSimpleName();
    
    /**
     * Called on receive of strokes updates.
     */
    @Override
    public void handleStrokeUpdate() {
        AppMem.GAME.update(protocol.getStrokes());
    }
    
    public void handleGuessUpdate() {
        String message = protocol.getSenderName() + ": " + protocol.getMessage();
        AppMem.GAME.updateChat(message);
        AppMem.GAME.updatePoints(protocol.getLeaderboard());
        AppMem.LEADERBOARD = protocol.getLeaderboard();
    }
    
    @Override
    public void handleStatusUpdate() {
    
    }
    
    @Override
    public void handleTimeout() {
    
    }
    
    @Override
    public void handleStartRound() {
        AppMem.BUFFER = protocol;
        handleBufferStartRound();
    }
    
    @Override
    public void handleStartDraw() {
        AppMem.DRAW_START = true;
        AppMem.GAME.resetBrush();
        AppMem.GAME.runOnUiThread(() -> AppMem.GAME.countdown(Const.GAME_TIME_LIMIT));
        if (AppMem.PAINTER) {
            AppMem.GAME.runOnUiThread(() -> AppMem.GAME.update());
        }
    }
    
    @Override
    public void handleGameFinish() {
        AppMem.GAME.startActivity(new Intent(AppMem.GAME, ResultActivity.class));
        AppMem.GAME.finish();
    }
}
