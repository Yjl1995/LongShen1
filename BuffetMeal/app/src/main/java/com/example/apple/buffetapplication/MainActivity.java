package com.example.apple.buffetapplication;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.View;
import android.widget.Toast;

import java.util.Timer;
import java.util.TimerTask;

public class MainActivity extends AppCompatActivity {
    static String Key = "NULL";
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Intent intent = getIntent();
        String KEY = intent.getStringExtra("KEY");
        System.out.printf("KEY = ",KEY);
        Key = KEY;
    }
    public void ToScan(View v){
        Intent intent = new Intent(this,ScanActivity.class);
        startActivity(intent);
    }
    public  void ToPersonPage(View v){
        Intent intent = new Intent(this,PersonPageActivity.class);
        intent.putExtra("KEY",Key);
        startActivity(intent);
    }
    private int keyBackClickCount = 0;

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK) {
            switch (keyBackClickCount++) {
                case 0:
                    Toast.makeText(this, "再次按返回键退出", Toast.LENGTH_SHORT).show();
                    Timer timer = new Timer();
                    timer.schedule(new TimerTask() {
                        @Override
                        public void run() {
                            keyBackClickCount = 0;
                        }
                    }, 3000);
                    break;
                case 1:
//                    EMChatManager.getInstance().logout();// 退出环信聊天
//                    App.getInstance2().exit();
                    finish();
//                    overridePendingTransition(R.anim.push_up_in, R.anim.push_up_out);
                    break;
            }
            return true;
        }
        return super.onKeyDown(keyCode, event);
    }

}
