package com.teamluck.drawdiculous.app.activity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.teamluck.drawdiculous.R;
import com.teamluck.drawdiculous.app.handler.AppGameHandler;
import com.teamluck.drawdiculous.app.handler.AppRoomHandler;
import com.teamluck.drawdiculous.app.model.User;
import com.teamluck.drawdiculous.app.utils.AppConst;
import com.teamluck.drawdiculous.app.utils.AppMem;
import com.teamluck.drawdiculous.backend.client.handler.ClientHandler;
import com.teamluck.drawdiculous.backend.client.netty.Client;
import com.teamluck.drawdiculous.backend.client.netty.ClientSender;
import com.teamluck.drawdiculous.backend.client.utils.ClientMemory;

/**
 * Home activity, allows users to choose from Create, Join, Join random.
 * TODO: other details such as Settings, Logout
 */
public class HomeActivity extends AppCompatActivity {
    
    public static final String TAG = HomeActivity.class.getSimpleName();
    
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        
        // initialize some crucial data
        loadAndConnect();
        ClientHandler.clientRoomHandler = new AppRoomHandler();
        ClientHandler.clientGameHandler = new AppGameHandler();
        
        // saves the context for UI updates upon receiving response
        AppMem.CURRENT_ACTIVITY = this;
        
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);
        
        // create
        findViewById(R.id.create_game_id).setOnClickListener(v -> startActivity(new Intent(HomeActivity.this, CreateRoomActivity.class)));
        
        // join
        findViewById(R.id.join_game_id).setOnClickListener(v -> startActivity(new Intent(HomeActivity.this, JoinRoomActivity.class)));
        
        // server-side code calls for join an random game
        findViewById(R.id.join_random_game_id).setOnClickListener(v -> {
            ClientSender.requestJoinRandomRoom();
            AppMem.APP_STATUS = AppConst.WAITING_FOR_RANDOM_JOIN;
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
    
    /**
     * Loads user details from Firebase Realtime Database.
     * Attempt to connect to server upon completion.
     */
    private void loadAndConnect() {
        FirebaseAuth fAuth = FirebaseAuth.getInstance();
        FirebaseDatabase fDatabase = FirebaseDatabase.getInstance(AppConst.DB_URL);
        FirebaseUser fUser = fAuth.getCurrentUser();
        assert fUser != null;
        
        DatabaseReference sRef = fDatabase.getReference("Users");
        DatabaseReference uRef = sRef.child(fUser.getUid());
        uRef.get().addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                DataSnapshot snapshot = task.getResult();
                assert snapshot != null;
                User.setInstance(snapshot.getValue(User.class));
                Log.d(TAG, "loadUserData: successfully fetch data");
                
                User user = User.getInstance();
                ClientMemory.userName = user.getUsername();
                ClientMemory.userId = user.getId();
                
                connectToServer();
            }
            else {
                Log.e(TAG, "Error getting data", task.getException());
            }
        });
    }
    
    /**
     * Connects to the server and informs the server about the user identity.
     */
    private void connectToServer() {
        try {
            Client.run();
        }
        catch (InterruptedException e) {
            e.printStackTrace();
            Toast.makeText(this, "Connection failed", Toast.LENGTH_LONG).show();
        }
        ClientSender.requestEstablishConnection();
    }
}
