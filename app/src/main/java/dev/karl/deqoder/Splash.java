package dev.karl.deqoder;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.widget.MediaController;
import android.widget.VideoView;

public class Splash extends AppCompatActivity {

    VideoView videoView;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);

        getWindow().setStatusBarColor(ContextCompat.getColor(Splash.this, R.color.white));
        getWindow().setNavigationBarColor(ContextCompat.getColor(Splash.this, R.color.white));

        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                //
                getWindow().setStatusBarColor(ContextCompat.getColor(Splash.this, R.color.dark_gray));
                getWindow().setNavigationBarColor(ContextCompat.getColor(Splash.this, R.color.dark_gray));
            }
        }, 4300);

        VideoView videoView = findViewById(R.id.videoView);
        String videoPath = "android.resource://" + getPackageName() + "/" + R.raw.splash; // Replace with your video file
        Uri uri = Uri.parse(videoPath);

        // Set up the VideoView
        videoView.setVideoURI(uri);

        // Set a listener to detect when the video playback is complete
        videoView.setOnCompletionListener(mp -> {
            // Start a new activity after the video playback is complete
            Intent intent = new Intent(Splash.this, Home.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
            startActivity(intent);
        });

        // Start playing the video
        videoView.start();
    }
}