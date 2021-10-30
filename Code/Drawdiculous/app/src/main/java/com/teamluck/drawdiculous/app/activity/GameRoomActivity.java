package com.teamluck.drawdiculous.app.activity;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.graphics.Typeface;
import android.os.Bundle;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.teamluck.drawdiculous.R;
import com.teamluck.drawdiculous.app.utils.AppConst;
import com.teamluck.drawdiculous.app.utils.AppMem;
import com.teamluck.drawdiculous.app.utils.SafeOnClickListener;
import com.teamluck.drawdiculous.backend.client.netty.ClientSender;
import com.teamluck.drawdiculous.backend.client.utils.ClientMemory;

import java.util.ArrayList;
import java.util.List;

/**
 * Game room activity, displays all users in a specific game room.
 * Host should be able to start game or kick any other players.
 * Players should be able to leave room. If the player is the host,
 * the room is destroyed.
 */
public class GameRoomActivity extends AppCompatActivity {
    
    private static final String TAG = GameRoomActivity.class.getSimpleName();
    
    private final List<String> users = new ArrayList<>();
    
    private LinearLayout parentLinearLayout;
    
    @SuppressLint("SetTextI18n")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        
        // saves the context for UI updates upon receiving response
        AppMem.CURRENT_ACTIVITY = this;
        AppMem.APP_STATUS = AppConst.WAITING_FOR_UPDATE;
        
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_game_room);
        
        TextView roomId = findViewById(R.id.roomIdText);
        roomId.setText("Room ID: " + ClientMemory.roomId);
        
        setUsers();
        
        Button startGameBtn = findViewById(R.id.button_start_game);
        
        // allow the user to start playing only if the user is the host
        if (!AppMem.HOST) {
            startGameBtn.setVisibility(View.GONE);
        }
        
        // request the server to start game
        startGameBtn.setOnClickListener(new SafeOnClickListener() {
            @Override
            public void onOneClick(View v) {
                if (AppMem.USERNAMES.size() > 1) {
                    ClientSender.requestStartGame();
                }
            }
        });
        
        findViewById(R.id.button_leave_room).setOnClickListener(new SafeOnClickListener() {
            @Override
            public void onOneClick(View v) {
                leave();
            }
        });
    }
    
    public void setUsers() {
        ArrayList<String> usernames = AppMem.USERNAMES;
        
        parentLinearLayout = findViewById(R.id.parent_player);
        TextView hostName = parentLinearLayout.findViewById(R.id.player_username);
        hostName.setText(usernames.get(0));
        users.add(usernames.get(0));
        hostName.setTypeface(hostName.getTypeface(), Typeface.BOLD_ITALIC);
        Button btn = parentLinearLayout.findViewById(R.id.delete_button);
        if (!AppMem.HOST) {
            btn.setVisibility(View.GONE);
        }
        
        // define players
        for (String name : usernames) {
            if (name.equals(usernames.get(0))) {
                continue;
            }
            users.add(name);
            // inflate (create) copies of player field
            LayoutInflater inflater = (LayoutInflater) getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            @SuppressLint("InflateParams") View rowView = inflater.inflate(R.layout.field, null);
            TextView names = rowView.findViewById(R.id.player_username);
            names.setText(name);
            Button child_btn = rowView.findViewById(R.id.delete_button);
            if (!AppMem.HOST) {
                child_btn.setVisibility(View.GONE);
            }
            // Add the new row before the add field button.
            parentLinearLayout.addView(rowView, parentLinearLayout.getChildCount());
        }
    }
    
    public void onKickClick(View v) {
        View textP = (View) v.getParent();
        TextView user = textP.findViewById(R.id.player_username);
        String username = user.getText().toString();
        
        if (username.equals(ClientMemory.userName)) {
            return;
        }
        ClientSender.requestDeleteUser(AppMem.USERNAME_TO_ID.getOrDefault(username, 0));
    }
    
    public void updateUsers() {
        for (int i = 0; i < users.size(); i++) {
            if (!AppMem.USERNAMES.contains(users.get(i))) {
                parentLinearLayout.removeViewAt(i);
                users.remove(i);
                return;
            }
        }
        for (String name : AppMem.USERNAMES) {
            if (!users.contains(name)) {
                users.add(name);
                // inflate (create) copies of player field
                LayoutInflater inflater = (LayoutInflater) getSystemService(Context.LAYOUT_INFLATER_SERVICE);
                @SuppressLint("InflateParams") View rowView = inflater.inflate(R.layout.field, null);
                TextView names = rowView.findViewById(R.id.player_username);
                names.setText(name);
                Button child_btn = rowView.findViewById(R.id.delete_button);
                if (!AppMem.HOST) {
                    child_btn.setVisibility(View.GONE);
                }
                // Add the new row before the add field button.
                parentLinearLayout.addView(rowView, parentLinearLayout.getChildCount());
                return;
            }
        }
    }
    
    private void leave() {
        if (AppMem.HOST) {
            ClientSender.requestDeleteRoom();
        }
        else {
            ClientSender.requestLeaveRoom();
        }
    }
    
    @Override
    protected void onPause() {
        super.onPause();
        Log.d(TAG, "onPause: activity paused");
    }
    
    @Override
    protected void onResume() {
        AppMem.CURRENT_ACTIVITY = this;
        super.onResume();
        Log.d(TAG, "onPause: activity resumed");
    }
    
    @Override
    protected void onDestroy() {
        if (AppMem.CURRENT_ACTIVITY == this) {
            AppMem.CURRENT_ACTIVITY = null;
        }
        super.onDestroy();
    }
    
    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK) {
            leave();
            startActivity(new Intent(this, HomeActivity.class));
            finish();
        }
        return super.onKeyDown(keyCode, event);
    }
}