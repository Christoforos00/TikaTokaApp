package gr.aueb.tikatokaapp.View;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.widget.MediaController;
import android.widget.VideoView;

import gr.aueb.tikatokaapp.R;

public class TestActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_test);
        VideoView videoView = (VideoView) findViewById(R.id.videoViewPlayer);
        String extra = this.getIntent().getStringExtra("PATH");
        videoView.setVideoPath(extra);
        MediaController mediaController = new MediaController(this);
        mediaController.setAnchorView(videoView);
        videoView.setMediaController(mediaController);

    }
}