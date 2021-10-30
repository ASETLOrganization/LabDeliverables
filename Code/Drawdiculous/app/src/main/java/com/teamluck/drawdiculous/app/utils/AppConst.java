package com.teamluck.drawdiculous.app.utils;

/**
 * Some constants used in the application.
 */
public class AppConst {
    
    public static final String DB_URL = "https://drawdiculous-default-rtdb.asia-southeast1.firebasedatabase.app";
    
    public static final long TOO_SOON_DURATION_MS = 700;
    public static final int SPLASH_DISPLAY_LENGTH = 1000;
    
    public static final int NULL_STATE = -1;
    public static final int WAITING_FOR_CREATE = 100;
    public static final int WAITING_FOR_JOIN = 101;
    public static final int WAITING_FOR_RANDOM_JOIN = 102;
    public static final int WAITING_FOR_UPDATE = 103;
    
    public static final int UPDATE_INTERVAL = 50;
    
    public static final int OP_DRAW_ACTION_COLOR = 3;
    public static final int OP_DRAW_ACTION_BRUSH = 4;
    public static final int OP_DRAW_ACTION_CLEAR = 5;
}
