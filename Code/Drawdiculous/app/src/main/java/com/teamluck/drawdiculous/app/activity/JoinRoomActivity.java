package com.teamluck.drawdiculous.app.activity;

import android.os.Bundle;
import android.view.KeyEvent;
import android.view.View;
import android.widget.EditText;

import androidx.appcompat.app.AppCompatActivity;

import com.teamluck.drawdiculous.R;
import com.teamluck.drawdiculous.app.utils.AppConst;
import com.teamluck.drawdiculous.app.utils.AppMem;
import com.teamluck.drawdiculous.app.utils.SafeOnClickListener;
import com.teamluck.drawdiculous.backend.client.netty.ClientSender;

/**
 * Join room activity prompts for user inputs for joining a game room.
 */
public class JoinRoomActivity extends AppCompatActivity {
    
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        
        // saves the context for UI updates upon receiving response
        AppMem.CURRENT_ACTIVITY = this;
        
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_join_room);
        
        // send request to server onclick
        findViewById(R.id.join_game_room_id).setOnClickListener(new SafeOnClickListener() {
            @Override
            public void onOneClick(View v) {
                EditText editTextId = findViewById(R.id.input_room_id);
                int roomId = Integer.parseInt(editTextId.getText().toString());
                
                EditText editTextPwd = findViewById(R.id.input_join_password);
                String roomPassword = editTextPwd.getText().toString();
                
                ClientSender.requestJoinRoom(roomId, roomPassword);
                AppMem.APP_STATUS = AppConst.WAITING_FOR_JOIN;
            }
        });
    }
    
    @Override
    protected void onResume() {
        AppMem.CURRENT_ACTIVITY = this;
        super.onResume();
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
            finish();
        }
        return super.onKeyDown(keyCode, event);
    }
}
