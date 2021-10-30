package com.teamluck.drawdiculous.app.utils;

import android.annotation.SuppressLint;

import androidx.appcompat.app.AppCompatActivity;

import com.teamluck.drawdiculous.app.activity.DrawActivity;
import com.teamluck.drawdiculous.backend.protocol.GameProtocol;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

/**
 * Some global data.
 */
public class AppMem {
    
    public static boolean HOST = false;
    public static boolean PAINTER = false;
    public static boolean DRAW_START = false;
    public static int APP_STATUS = AppConst.NULL_STATE;
    
    public static GameProtocol BUFFER = null;
    
    public static ArrayList<String> USERNAMES = null;
    
    @SuppressLint("StaticFieldLeak")
    public static DrawActivity GAME = null;
    public static AppCompatActivity CURRENT_ACTIVITY = null;
    
    public static Map<String, Integer> USERNAME_TO_ID = new HashMap<>();
    
    public static Map<String, Integer> LEADERBOARD = new HashMap<>();
    
}
