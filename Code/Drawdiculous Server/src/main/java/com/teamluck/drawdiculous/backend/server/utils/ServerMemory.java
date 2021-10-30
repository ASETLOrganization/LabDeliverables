package com.teamluck.drawdiculous.backend.server.utils;

import com.teamluck.drawdiculous.backend.Const;
import com.teamluck.drawdiculous.backend.model.Room;
import com.teamluck.drawdiculous.backend.model.User;
import io.netty.channel.Channel;

import java.util.HashSet;
import java.util.Iterator;
import java.util.Random;
import java.util.Set;
import java.util.logging.Logger;

/**
 * Stores global server data
 */
public class ServerMemory {
    
    public static final Set<User> USERS = new HashSet<>();
    public static final Set<Room> ROOMS = new HashSet<>();
    public static final Set<Room> PUBLIC_ROOMS = new HashSet<>();
    public static final Set<Room> PRIVATE_ROOMS = new HashSet<>();
    private static final Logger logger = Logger.getLogger(ServerMemory.class.getName());
    
    /**
     * Get user in room given user's id.
     */
    public static User getUser(int id) {
        for (User user : USERS) {
            if (user.getId() == id) {
                return user;
            }
        }
        return null;
    }
    
    /**
     * Get user given user's channel.
     */
    public static User getUser(Channel channel) {
        for (User user : USERS) {
            if (user.getChannel() == channel) {
                return user;
            }
        }
        return null;
    }
    
    /**
     * Get room given room's id.
     */
    public static Room getRoom(int id) {
        for (Room room : ROOMS) {
            if (room.getId() == id) {
                return room;
            }
        }
        return null;
    }
    
    /**
     * Get room given user's id.
     */
    public static Room getUserRoom(User user) {
        for (Room room : ROOMS) {
            if (room.contains(user)) {
                return room;
            }
        }
        return null;
    }
    
    /**
     * Add a new private room.
     */
    public static void addPrivateRoom(Room room) {
        ROOMS.add(room);
        PRIVATE_ROOMS.add(room);
    }
    
    /**
     * Add a new public room.
     */
    public static void addPublicRoom(Room room) {
        ROOMS.add(room);
        PUBLIC_ROOMS.add(room);
    }
    
    /**
     * Add a new room.
     */
    public static void addRoom(Room room) {
        if (room.isPublic()) {
            addPublicRoom(room);
        }
        else {
            addPrivateRoom(room);
        }
    }
    
    /**
     * Remove a room.
     */
    public static void removeRoom(Room room) {
        assert room != null;
        PUBLIC_ROOMS.removeIf(r -> r.getId() == room.getId());
        PRIVATE_ROOMS.removeIf(r -> r.getId() == room.getId());
        ROOMS.removeIf(r -> r.getId() == room.getId());
    }
    
    /**
     * Get all available public rooms.
     */
    private static Set<Room> getAvailablePublicRooms() {
        Set<Room> set = new HashSet<>();
        for (Room room : PUBLIC_ROOMS) {
            if (room.notFull() && room.getStatus() == Const.ROOM_STATUS_WAITING) {
                set.add(room);
            }
        }
        return set;
    }
    
    /**
     * Get a random public room.
     */
    public static Room getRandomPublicRoom() {
        Set<Room> availableRooms = getAvailablePublicRooms();
        if (availableRooms.isEmpty()) {
            return null;
        }
        int index = new Random().nextInt(availableRooms.size());
        Iterator<Room> iterator = PUBLIC_ROOMS.iterator();
        for (int i = 0; i < index; i++) {
            iterator.next();
        }
        return iterator.next();
    }
}
