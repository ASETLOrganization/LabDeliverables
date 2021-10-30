package com.teamluck.drawdiculous.app.handler;

import android.content.Intent;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.teamluck.drawdiculous.R;
import com.teamluck.drawdiculous.app.activity.DrawActivity;
import com.teamluck.drawdiculous.app.activity.GameRoomActivity;
import com.teamluck.drawdiculous.app.activity.HomeActivity;
import com.teamluck.drawdiculous.app.utils.AppConst;
import com.teamluck.drawdiculous.app.utils.AppMem;
import com.teamluck.drawdiculous.backend.Const;
import com.teamluck.drawdiculous.backend.client.handler.ClientRoomHandler;
import com.teamluck.drawdiculous.backend.client.utils.ClientMemory;

/**
 * App room handler, handles server packages about updates in the room.
 */
public class AppRoomHandler extends ClientRoomHandler {
    
    /**
     * Handles responses after creation of room.
     */
    @Override
    public void handleCreate() {
        if (AppMem.APP_STATUS != AppConst.WAITING_FOR_CREATE) {
            return;
        }
        AppMem.APP_STATUS = AppConst.NULL_STATE;
        
        ClientMemory.roomId = protocol.getRoomId();
        AppMem.USERNAMES = protocol.getUsers();
        AppMem.HOST = true;
        AppMem.USERNAME_TO_ID.put(protocol.getSenderName(), protocol.getSenderId());
        AppCompatActivity aca = AppMem.CURRENT_ACTIVITY;
        aca.runOnUiThread(() -> Toast.makeText(aca, "Successfully created new room", Toast.LENGTH_SHORT).show());
        aca.startActivity(new Intent(aca, GameRoomActivity.class));
        aca.finish();
    }
    
    /**
     * Handles responses after requesting to join a room.
     */
    @Override
    public void handleJoin() {
        if (AppMem.APP_STATUS != AppConst.WAITING_FOR_JOIN && AppMem.APP_STATUS != AppConst.WAITING_FOR_RANDOM_JOIN) {
            return;
        }
        AppMem.APP_STATUS = AppConst.NULL_STATE;
        
        switch (protocol.getStatus()) {
            case Const.STATUS_OK: {
                ClientMemory.roomId = protocol.getRoomId();
                AppMem.USERNAMES = protocol.getUsers();
                AppMem.USERNAME_TO_ID.put(protocol.getSenderName(), protocol.getSenderId());
                AppCompatActivity aca = AppMem.CURRENT_ACTIVITY;
                aca.startActivity(new Intent(aca, GameRoomActivity.class));
                aca.finish();
                break;
            }
            case Const.STATUS_NOT_FOUND: {
                AppMem.CURRENT_ACTIVITY.runOnUiThread(() -> Toast.makeText(AppMem.CURRENT_ACTIVITY, R.string.joinStatusNotFoundResponse, Toast.LENGTH_SHORT).show());
                break;
            }
            case Const.STATUS_FORBIDDEN: {
                AppMem.CURRENT_ACTIVITY.runOnUiThread(() -> Toast.makeText(AppMem.CURRENT_ACTIVITY, R.string.joinStatusForbiddenResponse, Toast.LENGTH_SHORT).show());
                break;
            }
            default: {
                break;
            }
        }
    }
    
    /**
     * Handles updates for the room player list.
     */
    @Override
    public void handleUpdate() {
        if (AppMem.APP_STATUS != AppConst.WAITING_FOR_UPDATE) {
            return;
        }
        
        switch (protocol.getStatus()) {
            case Const.STATUS_OK: {
                AppMem.USERNAMES = protocol.getUsers();
                AppMem.USERNAME_TO_ID.put(protocol.getSenderName(), protocol.getSenderId());
                AppMem.CURRENT_ACTIVITY.runOnUiThread(() -> ((GameRoomActivity) AppMem.CURRENT_ACTIVITY).updateUsers());
                break;
            }
            case Const.STATUS_NOT_FOUND: {
                AppMem.CURRENT_ACTIVITY.runOnUiThread(() -> Toast.makeText(AppMem.CURRENT_ACTIVITY, R.string.playerNotFoundResponse, Toast.LENGTH_SHORT).show());
                break;
            }
            case Const.STATUS_FORBIDDEN: {
                AppMem.CURRENT_ACTIVITY.runOnUiThread(() -> Toast.makeText(AppMem.CURRENT_ACTIVITY, R.string.playerForbiddenResponse, Toast.LENGTH_SHORT).show());
                break;
            }
            default: {
                break;
            }
        }
    }
    
    /**
     * Handles updates for user leave room status.
     */
    @Override
    public void handleLeave() {
        if (AppMem.APP_STATUS != AppConst.WAITING_FOR_UPDATE) {
            return;
        }
        
        switch (protocol.getStatus()) {
            case Const.STATUS_OK: {
                AppMem.HOST = false;
                AppMem.USERNAMES = null;
                ClientMemory.roomId = -1;
                AppCompatActivity aca = AppMem.CURRENT_ACTIVITY;
                aca.startActivity(new Intent(aca, HomeActivity.class));
                aca.finish();
                break;
            }
            case Const.STATUS_NOT_FOUND: {
                AppMem.CURRENT_ACTIVITY.runOnUiThread(() -> Toast.makeText(AppMem.CURRENT_ACTIVITY, "room not found", Toast.LENGTH_SHORT).show());
                break;
            }
            case Const.STATUS_FORBIDDEN: {
                AppMem.CURRENT_ACTIVITY.runOnUiThread(() -> Toast.makeText(AppMem.CURRENT_ACTIVITY, "you are not in the room", Toast.LENGTH_SHORT).show());
                break;
            }
            default: {
                break;
            }
        }
    }
    
    /**
     * Handles updates on game start
     */
    @Override
    public void handleStartGame() {
        AppCompatActivity aca = AppMem.CURRENT_ACTIVITY;
        aca.startActivity(new Intent(aca, DrawActivity.class));
        aca.finish();
    }
}
