package com.teamluck.drawdiculous.app.handler;

import com.teamluck.drawdiculous.app.utils.AppMem;
import com.teamluck.drawdiculous.backend.Const;
import com.teamluck.drawdiculous.backend.client.utils.ClientMemory;

public class AppBufferHandler {
    
    private static final String TAG = AppBufferHandler.class.getSimpleName();
    
    public synchronized static void handleBufferStartRound() {
        if (AppMem.GAME == null || AppMem.BUFFER == null) {
            return;
        }
        AppMem.DRAW_START = false;
        AppMem.PAINTER = ClientMemory.userId == AppMem.BUFFER.getPainterId();
        AppMem.GAME.clearScreen();
        AppMem.GAME.displayWord(AppMem.BUFFER.getWord());
        AppMem.GAME.runOnUiThread(() -> AppMem.GAME.countdown(Const.GAME_PREPARE_LIMIT));
        AppMem.BUFFER = null;
    }
    
}
