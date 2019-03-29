package com.zhang.floatwindow;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

//这是应用内悬浮窗
public class MainActivity extends AppCompatActivity {
private TextView open;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        open=findViewById(R.id.open);
        open.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (!utils.isServiceRunning(MainActivity.this, "com.zhang.floatwindow.FloatService"))
                {
                    startService(new Intent(MainActivity.this, FloatService.class));
                    utils.saveStringToMainSP(MainActivity.this, CommParams.SP_FLOAT_BTN_FIRST, "first");
                }else {
                    Toast.makeText(MainActivity.this,"已经开启了悬浮窗",Toast.LENGTH_SHORT).show();
                }
            }
        });

    }

    @Override
    protected void onPause() {
        super.onPause();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
//        stopService(new Intent(this,FloatService.class));
    }
}
