package com.teamluck.drawdiculous.backend.protocol;

import com.teamluck.drawdiculous.backend.Const;
import com.teamluck.drawdiculous.backend.model.DrawAction;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

/**
 * Game protocol
 */
@Data
@EqualsAndHashCode(callSuper = true)
public class GameProtocol extends Protocol {
    
    // time left for the painter
    private int timeout;
    
    // guesses users send
    private String message;
    
    // title of the drawing
    private String word;
    
    private String painterName;
    private int painterId;
    
    private Map<String, Integer> leaderboard;
    
    private ArrayList<DrawAction> strokes;
    
    /**
     * Constructor for game protocol.
     */
    public GameProtocol() {
        setType(Const.TYPE_GAME);
        leaderboard = new HashMap<>();
        strokes = null;
    }
}
