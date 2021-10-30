package com.teamluck.drawdiculous.backend.server.utils;

import com.teamluck.drawdiculous.backend.Const;

import java.util.Random;

/**
 * Provide utilities for server
 */
public class ServerUtils {
    
    /**
     * Generate unique room id
     */
    public static int generateRoomId() {
        Random random = new Random();
        int id = random.nextInt(Const.ROOM_ID_BOUND);
        while (ServerMemory.getRoom(id) != null) {
            id = random.nextInt(Const.ROOM_ID_BOUND);
        }
        return id;
    }
    
}
