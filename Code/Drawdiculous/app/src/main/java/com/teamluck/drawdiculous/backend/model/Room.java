package com.teamluck.drawdiculous.backend.model;

import com.teamluck.drawdiculous.backend.Const;
import com.teamluck.drawdiculous.backend.server.netty.ServerSender;
import com.teamluck.drawdiculous.backend.server.utils.ServerMemory;
import io.netty.channel.group.ChannelGroup;
import io.netty.channel.group.DefaultChannelGroup;
import io.netty.util.concurrent.GlobalEventExecutor;
import lombok.Data;

import java.util.*;

/**
 * Stores room information.
 */
@Data
public class Room {
    
    // shared variables
    private int id;
    private int status;
    private ChannelGroup channels;
    private ArrayList<User> users;
    
    // room variables
    private String password;
    private User host;
    
    // game variables
    private int painterIndex;
    private long startTime;
    private boolean drawing;
    private User painter;
    private String word;
    private Set<User> guessedUsers;
    private Set<String> usedWords;
    private Map<String, Integer> leaderboard;
    
    /**
     * Room constructor
     */
    public Room() {
        users = new ArrayList<>();
        channels = new DefaultChannelGroup(GlobalEventExecutor.INSTANCE);
        leaderboard = new HashMap<>();
        drawing = false;
    }
    
    /**
     * Initialize a game when a new game is created.
     */
    public void startGame() {
        // collections that are maintained in a game
        status = Const.ROOM_STATUS_PLAYING;
        usedWords = new HashSet<>();
        leaderboard = new HashMap<>();
        for (User user : users) {
            leaderboard.put(user.getName(), 0);
        }
        initDrawing();
    }
    
    /**
     * Initialize variables every time a different player starts drawing.
     */
    private void initDrawing() {
        // game ends
        if (painterIndex == users.size()) {
            gameFinish();
            return;
        }
        
        drawing = false;
        guessedUsers = new HashSet<>();
        painter = users.get(painterIndex++);
        
        int wordIndex = new Random().nextInt(Const.WORDS.length);
        word = Const.WORDS[wordIndex];
        while (usedWords.contains(word)) {
            wordIndex = new Random().nextInt(Const.WORDS.length);
            word = Const.WORDS[wordIndex];
        }
        
        ServerSender.updateStartRound(this);
        scheduleTimeout(Const.GAME_PREPARE_LIMIT, this::startDrawing);
    }
    
    private void startDrawing() {
        drawing = true;
        startTime = System.currentTimeMillis();
        ServerSender.updateStartDraw(this);
        scheduleTimeout(Const.GAME_TIME_LIMIT, this::initDrawing);
    }
    
    /**
     * Finishes game when everyone in game has drawn.
     */
    private void gameFinish() {
        ServerSender.updateGameFinish(this);
        ServerMemory.removeRoom(this);
    }
    
    /**
     * Keep track of timer.
     * Timeout when time is up.
     */
    public void scheduleTimeout(long time, Runnable action) {
        new Thread(() -> {
            try {
                Thread.sleep(time);
            }
            catch (InterruptedException e) {
                e.printStackTrace();
            }
            action.run();
        }).start();
    }
    
    /**
     * Update leaderboard when a player's score changes.
     */
    public void updateLeaderboard(User user) {
        // update user's score in leaderboard
        leaderboard.put(user.getName(), leaderboard.get(user.getName()) + users.size() - guessedUsers.size());
        leaderboard.put(painter.getName(), leaderboard.get(painter.getName()) + Const.GAME_PAINTER_REWARD);
        // only add user into guessed when user answers correctly
        addGuessedUser(user);
    }
    
    /**
     * Get time remaining till timeout.
     */
    public int getTimeout() {
        return (int) (System.currentTimeMillis() - startTime);
    }
    
    /**
     * Add user to the room.
     */
    public void addUser(User user) {
        channels.add(user.getChannel());
        users.add(user);
    }
    
    /**
     * Remove user from the room.
     */
    public void removeUser(User user) {
        assert users.contains(user);
        channels.remove(user.getChannel());
        users.remove(user);
    }
    
    /**
     * Return true if room is public.
     */
    public boolean isPublic() {
        return "".equals(this.password);
    }
    
    /**
     * Return true if room is private.
     */
    public boolean isPrivate() {
        return !isPublic();
    }
    
    /**
     * Get all users in room.
     */
    public ArrayList<String> getUserNames() {
        ArrayList<String> names = new ArrayList<>();
        for (User user : users) {
            names.add(user.getName());
        }
        return names;
    }
    
    /**
     * Return true if password is wrong.
     */
    public boolean wrongPassword(String password) {
        if (password == null) {
            return true;
        }
        return !password.equals(this.password);
    }
    
    /**
     * Return true if room contains the user.
     */
    public boolean contains(User user) {
        for (User u : this.users) {
            if (u.getId() == user.getId()) {
                return true;
            }
        }
        return false;
    }
    
    /**
     * Return true if the room do not contain the user.
     */
    public boolean notContains(User user) {
        return !contains(user);
    }
    
    /**
     * Return true if room is full.
     */
    public boolean full() {
        return users.size() >= Const.ROOM_SIZE_LIMIT;
    }
    
    /**
     * Return true if room is not full.
     */
    public boolean notFull() {
        return !full();
    }
    
    /**
     * Add user to guessedUser if user already guessed correctly.
     */
    private void addGuessedUser(User user) {
        guessedUsers.add(user);
    }
    
    /**
     * Return true if user has guessed correctly before.
     */
    public boolean hasGuessed(User user) {
        return guessedUsers.contains(user);
    }
    
    /**
     * Return true if user has not guessed correctly before.
     */
    public boolean hasNotGuessed(User user) {
        return !hasGuessed(user);
    }
}
