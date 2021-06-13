package gr.aueb.tikatokaapp.View;

import androidx.appcompat.app.AppCompatActivity;

import android.media.MediaPlayer;
import android.os.Bundle;
import android.widget.MediaController;
import android.widget.VideoView;

import gr.aueb.tikatokaapp.R;

public class VideoPlayerActivity extends AppCompatActivity {
    int completionPos , errorPos , playerPos;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_video_player);
        String extra = this.getIntent().getStringExtra("PATH");
        MediaController mediaController = new MediaController(this);
        VideoView videoView = (VideoView) findViewById(R.id.videoViewPlayer);

//        videoView.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
//            @Override
//            public void onCompletion(MediaPlayer mp) {
//                errorPos = mp.getCurrentPosition();
//                playerPos=completionPos > errorPos ? completionPos : errorPos;
//
//                mp.reset();
//                videoView.setVideoPath(extra);
//                videoView.seekTo(playerPos);
//                videoView.start();
//            }
//        });
//
//        videoView.setOnErrorListener(new MediaPlayer.OnErrorListener() {
//            @Override
//            public boolean onError(MediaPlayer mp, int what, int extra) {
//                playerPos = mp.getCurrentPosition();
//                return true;
//            }
//        });


        videoView.setMediaController(mediaController);
        mediaController.setAnchorView(videoView);
        videoView.setVideoPath(extra);
        videoView.start();

    }

}