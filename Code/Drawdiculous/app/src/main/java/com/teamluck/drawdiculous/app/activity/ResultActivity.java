package com.teamluck.drawdiculous.app.activity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.teamluck.drawdiculous.R;
import com.teamluck.drawdiculous.app.utils.AppMem;
import com.teamluck.drawdiculous.app.utils.SafeOnClickListener;

import java.util.ArrayList;
import java.util.List;

public class ResultActivity extends AppCompatActivity {
    
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_result);
        
        List<String> names = new ArrayList<>(AppMem.LEADERBOARD.keySet());
        names.sort((n1, n2) -> AppMem.LEADERBOARD.get(n2).compareTo(AppMem.LEADERBOARD.get(n1)));
        
        int counter = 0;
        
        for (String name : names) {
            if (counter == 0) {
                ((TextView) findViewById(R.id.p1)).setText(name);
                ((TextView) findViewById(R.id.s1)).setText((AppMem.LEADERBOARD.getOrDefault(name, 0)).toString());
            }
            else if (counter == 1) {
                ((TextView) findViewById(R.id.p2)).setText(name);
                ((TextView) findViewById(R.id.s2)).setText((AppMem.LEADERBOARD.getOrDefault(name, 0)).toString());
            }
            else if (counter == 2) {
                ((TextView) findViewById(R.id.p3)).setText(name);
                ((TextView) findViewById(R.id.s3)).setText((AppMem.LEADERBOARD.getOrDefault(name, 0)).toString());
            }
            else {
                break;
            }
            counter++;
        }
        
        findViewById(R.id.button_result_back).setOnClickListener(new SafeOnClickListener() {
            @Override
            public void onOneClick(View v) {
                startActivity(new Intent(ResultActivity.this, HomeActivity.class));
                finish();
            }
        });
    }
}