package com.gzd.shadowprogressimageview;

import android.os.Bundle;
import android.view.View;

import androidx.appcompat.app.AppCompatActivity;

import java.util.Timer;
import java.util.TimerTask;

/**
 * @author GZDong
 */
@SuppressWarnings("AlibabaAvoidUseTimer")
public class MainActivity extends AppCompatActivity {

    private ShadowProgressImageView ivPic;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ivPic = findViewById(R.id.iv_pic);
        findViewById(R.id.btn_add).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ivPic.setProgress(ivPic.getProgress() + 10);
            }
        });

        findViewById(R.id.btn_download).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Timer timer = new Timer();
                timer.scheduleAtFixedRate(new TimerTask() {
                    @Override
                    public void run() {
                        MainActivity.this.runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                ivPic.setProgress(ivPic.getProgress() + 1);
                            }
                        });
                    }
                }, 1000, 100);
            }
        });
    }
}