package com.example.subrata.alexatestthree;

import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.view.ViewCompat;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;
import butterknife.ButterKnife;


public class ExoPlayerActivity extends Activity {

    Button buttonPlayUrlVideo;
    Button buttonPlayDefaultVideo;

    private String video_url = "http://commondatastorage.googleapis.com/gtv-videos-bucket/sample/BigBuckBunny.mp4";





    private void showDialogPromt() {
        Toast.makeText(this, "Showing Dialog", Toast.LENGTH_SHORT).show();
    }


    @RequiresApi(28)
    private static class OnUnhandledKeyEventListenerWrapper implements View.OnUnhandledKeyEventListener {
        private ViewCompat.OnUnhandledKeyEventListenerCompat mCompatListener;

        OnUnhandledKeyEventListenerWrapper(ViewCompat.OnUnhandledKeyEventListenerCompat listener) {
            this.mCompatListener = listener;
        }

        public boolean onUnhandledKeyEvent(View v, KeyEvent event) {
            return this.mCompatListener.onUnhandledKeyEvent(v, event);
        }
    }


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_exo_player);

        //getSupportActionBar().hide();


        buttonPlayUrlVideo = findViewById(R.id.buttonPlayUrlVideo);
        buttonPlayDefaultVideo = findViewById(R.id.buttonPlayDefaultVideo);


        buttonPlayUrlVideo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showDialogPromt();
            }
        });


        buttonPlayDefaultVideo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent mIntent = VideoPlayActivity.getStartIntent(ExoPlayerActivity.this, video_url);
                startActivity(mIntent);
            }
        });

    }
}
