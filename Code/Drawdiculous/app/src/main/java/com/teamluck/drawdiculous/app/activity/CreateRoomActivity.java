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
 * Create room activity, prompts users for (optional) room password and send requests to the server.
 */
public class CreateRoomActivity extends AppCompatActivity {
    
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        
        // saves the context for UI updates upon receiving response
        AppMem.CURRENT_ACTIVITY = this;
        
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_room);
        
        findViewById(R.id.create_game_room_id).setOnClickListener(new SafeOnClickListener() {
            @Override
            public void onOneClick(View v) {
                EditText edittext = findViewById(R.id.input_create_password);
                String roomPassword = edittext.getText().toString();
                // sends request to the server for room creation
                ClientSender.requestCreateRoom(roomPassword);
                // sets the application status
                AppMem.APP_STATUS = AppConst.WAITING_FOR_CREATE;
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