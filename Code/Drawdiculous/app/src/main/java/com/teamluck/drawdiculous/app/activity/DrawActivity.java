package com.teamluck.drawdiculous.app.activity;

import android.annotation.SuppressLint;
import android.graphics.Color;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.teamluck.drawdiculous.R;
import com.teamluck.drawdiculous.app.handler.AppBufferHandler;
import com.teamluck.drawdiculous.app.utils.AppConst;
import com.teamluck.drawdiculous.app.utils.AppMem;
import com.teamluck.drawdiculous.app.view.RealtimeCanvas;
import com.teamluck.drawdiculous.backend.Const;
import com.teamluck.drawdiculous.backend.client.netty.ClientSender;
import com.teamluck.drawdiculous.backend.model.DrawAction;

import java.util.ArrayList;
import java.util.Map;


/**
 * Draw activity, displays the main game UI, deals with different subviews in the UI.
 */
public class DrawActivity extends AppCompatActivity {
    
    private static final String TAG = DrawActivity.class.getSimpleName();
    
    public int counter;
    private TextView textView;
    private RealtimeCanvas realtimeCanvas;
    private TextView promptText;
    
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        
        // save the game activity context for UI updates.
        AppMem.GAME = this;
        
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_draw);
        
        realtimeCanvas = findViewById(R.id.IdCanvas);
        promptText = findViewById(R.id.whichWord);
        
        // TODO: answer check is on the server side
        // Done -  Send message to chat box and check if answer is correct
        Button answerButton = findViewById(R.id.answerButton);
        answerButton.setOnClickListener(v -> {
            // sends the message to server
            sendGuess();
        });
        
        AppBufferHandler.handleBufferStartRound();
        
        initLeaderboard();
    }
    
    /**
     * Creates a new thread that fires every AppConst.UPDATE_INTERVAL milliseconds,
     * until the round is over. On each tick, updates the server on new points drawn
     * during this interval.
     */
    public void update() {
        new CountDownTimer(Const.GAME_TIME_LIMIT, AppConst.UPDATE_INTERVAL) {
            public void onTick(long millisUntilFinished) {
                ArrayList<DrawAction> strokes = realtimeCanvas.getStroke();
                if (strokes == null || strokes.isEmpty()) {
                    return;
                }
                ClientSender.requestUpdateStrokes(strokes);
            }
            
            public void onFinish() {
            }
        }.start();
    }
    
    /**
     * Clients gets the new points from the server as an ArrayList of DrawAction instances.
     * Draw these new points on canvas on UI thread.
     *
     * @param stroke new data pushed by painter
     */
    public void update(ArrayList<DrawAction> stroke) {
        runOnUiThread(() -> realtimeCanvas.drawStroke(stroke));
    }
    
    public void updateChat(String string) {
        runOnUiThread(() -> enterChat(string));
    }
    
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater menuInflater = getMenuInflater();
        menuInflater.inflate(R.menu.menu_draw, menu);
        return true;
    }
    
    @Override
    @SuppressLint("NonConstantResourceId")
    public boolean onOptionsItemSelected(MenuItem item) {
        if (!AppMem.PAINTER) {
            return super.onOptionsItemSelected(item);
        }
        switch (item.getItemId()) {
            case R.id.IdBrush:
            case R.id.IdColors:
                realtimeCanvas.brushReset();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }
    
    @SuppressLint("NonConstantResourceId")
    public void onColorItemClick(MenuItem item) {
        if (!AppMem.PAINTER) {
            return;
        }
        switch (item.getItemId()) {
            case R.id.IdColorBlack:
                realtimeCanvas.colors(Color.BLACK);
                break;
            case R.id.IdColorGreen:
                realtimeCanvas.colors(Color.GREEN);
                break;
            case R.id.IdColorBlue:
                realtimeCanvas.colors(Color.BLUE);
                break;
            case R.id.IdColorRed:
                realtimeCanvas.colors(Color.RED);
                break;
            // TODO: probably remove Color.WHITE? Cause prone to bugs.
            case R.id.IdColorWhite:
                realtimeCanvas.colors(Color.WHITE);
                break;
        }
        item.setChecked(true);
    }
    
    // TODO: hmm why
    @SuppressLint("NonConstantResourceId")
    public void onBrushItemClick(MenuItem item) {
        if (!AppMem.PAINTER) {
            return;
        }
        switch (item.getItemId()) {
            case R.id.IdBrushOne:
                realtimeCanvas.sizeBrush(1);
                break;
            case R.id.IdBrushTwo:
                realtimeCanvas.sizeBrush(2);
                break;
            case R.id.IdBrushFour:
                realtimeCanvas.sizeBrush(4);
                break;
            case R.id.IdBrushSix:
                realtimeCanvas.sizeBrush(6);
                break;
            case R.id.IdBrushEight:
                realtimeCanvas.sizeBrush(8);
                break;
            case R.id.IdBrushTen:
                realtimeCanvas.sizeBrush(10);
                break;
        }
        item.setChecked(true);
    }
    
    @SuppressLint("NonConstantResourceId")
    public void onEraserItemClick(MenuItem item) {
        if (!AppMem.PAINTER) {
            return;
        }
        switch (item.getItemId()) {
            case R.id.IdEraserOne:
                realtimeCanvas.eraser(1);
                break;
            case R.id.IdEraserTwo:
                realtimeCanvas.eraser(2);
                break;
            case R.id.IdEraserFour:
                realtimeCanvas.eraser(4);
                break;
            case R.id.IdEraserSix:
                realtimeCanvas.eraser(6);
                break;
            case R.id.IdEraserEight:
                realtimeCanvas.eraser(8);
                break;
            case R.id.IdEraserTen:
                realtimeCanvas.eraser(10);
                break;
        }
        item.setChecked(true);
    }
    
    @SuppressLint("NonConstantResourceId")
    public void onBinItemClick(MenuItem item) {
        if (!AppMem.PAINTER) {
            return;
        }
        realtimeCanvas.requestClearScreen();
    }
    
    public void countdown(long seconds) {
        textView = findViewById(R.id.timer);
        new CountDownTimer(seconds, 1000) {
            public void onTick(long millisUntilFinished) {
                counter = (int) millisUntilFinished / 1000;
                textView.setText(String.valueOf(counter));
            }
            
            public void onFinish() {
            }
        }.start();
    }
    
    public void displayWord(String word) {
        runOnUiThread(() -> promptText.setText(word));
    }
    
    // Sends message to server
    public void sendGuess() {
        TextView messageView = this.findViewById(R.id.AnswerBox);
        String guess = messageView.getText().toString();
        ClientSender.requestSendGuess(guess);
        messageView.setText("");
    }
    
    // Sends message to chat box
    public void enterChat(String string) {
        LinearLayout chatLinear = this.findViewById(R.id.linearChat);
        TextView message = new TextView(this);
        message.setBackgroundColor(Color.LTGRAY);
        message.setText(string);
        chatLinear.addView(message);
    }
    
    public void initLeaderboard() {
        LinearLayout playerList = this.findViewById(R.id.playerList);
        LinearLayout pointList = this.findViewById(R.id.pointList);
        for (String name : AppMem.USERNAMES) {
            TextView username = new TextView(this);
            username.setText(name);
            playerList.addView(username);
            
            TextView points = new TextView(this);
            points.setText("0");
            points.setTag(name);
            pointList.addView(points);
            AppMem.LEADERBOARD.put(name, 0);
        }
    }
    
    public void updatePoints(Map<String, Integer> leaderboard) {
        LinearLayout pointList = this.findViewById(R.id.pointList);
        for (Map.Entry<String, Integer> entry : leaderboard.entrySet()) {
            TextView pointText = pointList.findViewWithTag(entry.getKey());
            pointText.setText(String.valueOf(entry.getValue()));
        }
    }
    
    public void clearScreen() {
        runOnUiThread(() -> realtimeCanvas.clearScreen());
    }
    
    public void resetBrush() {
        realtimeCanvas.colors(Color.BLACK);
        realtimeCanvas.sizeBrush(1);
    }
}
