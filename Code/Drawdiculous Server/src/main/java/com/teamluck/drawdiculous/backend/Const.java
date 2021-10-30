package com.teamluck.drawdiculous.backend;

/**
 * stores global constants
 */
public class Const {
    
    public static final int PORT = 3002;
    public static final String HOST = "172.21.148.166";
    public static final int SERVER_ID = -3002;
    public static final String SERVER_NAME = "DRAWDICULOUS";
    
    public static final int INT_NULL_VALUE = -1;
    
    public static final int ROOM_ID_BOUND = 1000000;
    public static final int ROOM_SIZE_LIMIT = 6;
    
    public static final long GAME_TIME_LIMIT = 90 * 1000L;
    public static final long GAME_PREPARE_LIMIT = 5 * 1000L;
    public static final int GAME_PAINTER_REWARD = 2;
    
    public static final int STATUS_OK = 200;
    public static final int STATUS_FORBIDDEN = 403;
    public static final int STATUS_NOT_FOUND = 404;
    
    // Room: status
    public static final int ROOM_STATUS_WAITING = 0;
    public static final int ROOM_STATUS_PLAYING = 1;
    public static final int ROOM_STATUS_CLOSED = 2;
    
    // Protocol: type
    public static final int TYPE_ESTABLISH_CONNECTION = 1000;
    public static final int TYPE_ROOM = 1001;
    public static final int TYPE_GAME = 1002;
    
    // Establish: opcode
    public static final int OP_ESTABLISH_REQUEST = 10;
    public static final int OP_ESTABLISH_RESPONSE = 20;
    
    // Protocol: opCode
    public static final int OP_ROOM_CREATE = 30;
    public static final int OP_ROOM_DELETE = 31;
    public static final int OP_ROOM_JOIN = 32;
    public static final int OP_ROOM_RANDOM_JOIN = 33;
    public static final int OP_ROOM_LEAVE = 34;
    public static final int OP_ROOM_DELETE_USER = 35;
    public static final int OP_ROOM_START_GAME = 36;
    public static final int OP_ROOM_START_ROUND = 37;
    
    public static final int OP_ROOM_RESPONSE_CREATE = 40;
    public static final int OP_ROOM_RESPONSE_JOIN = 41;
    public static final int OP_ROOM_RESPONSE_UPDATE = 42;
    public static final int OP_ROOM_RESPONSE_LEAVE = 43;
    public static final int OP_ROOM_RESPONSE_START_GAME = 44;
    
    public static final int OP_GAME_STROKE = 50;
    public static final int OP_GAME_GUESS = 51;
    
    public static final int OP_GAME_UPDATE_STROKE = 60;
    public static final int OP_GAME_UPDATE_GUESS = 61;
    public static final int OP_GAME_UPDATE_STATUS = 62;
    public static final int OP_GAME_UPDATE_TIMEOUT = 63;
    public static final int OP_GAME_UPDATE_START_ROUND = 64;
    public static final int OP_GAME_UPDATE_START_DRAW = 65;
    public static final int OP_GAME_UPDATE_GAME_FINISH = 66;
    
    public static final String MESSAGE_CORRECT_ANSWER = "Bingo!";
    
    public static final String[] WORDS = new String[] {"Angel", "Eyeball", "Pizza", "Angry", "Fireworks", "Pumpkin", "Baby", "Flower", "Rainbow", "Beard", "Flying saucer", "Recycle", "Bible", "Giraffe", "Sand castle", "Bikini", "Glasses", "Snowflake", "Book", "High heel", "Stairs", "Bucket", "Ice cream cone", "Starfish", "Bumble bee", "Igloo", "Strawberry", "Butterfly", "Lady bug", "Sun", "Camera", "Lamp", "Tire", "Cat", "Lion", "Toast", "Church", "Mailbox", "Toothbrush", "Crayon", "Night", "Toothpaste", "Dolphin", "Nose", "Truck", "Egg", "Olympics", "Volleyball", "Eiffel Tower", "Peanut"};
}

