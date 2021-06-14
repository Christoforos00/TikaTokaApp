package gr.aueb.tikatokaapp.View;

import androidx.appcompat.app.AppCompatActivity;

import android.media.MediaPlayer;
import android.os.Bundle;
import android.widget.MediaController;
import android.widget.VideoView;

import gr.aueb.tikatokaapp.R;

public class VideoPlayerActivity extends AppCompatActivity {
    int completionPos, errorPos, playerPos;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_video_player);
        String PATH_VIDEO = this.getIntent().getStringExtra("PATH");
        MediaController mediaController = new MediaController(this);
        VideoView videoView = (VideoView) findViewById(R.id.videoViewPlayer);

        videoView.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
            @Override
            public void onCompletion(MediaPlayer mp) {
                completionPos = mp.getCurrentPosition();
//                playerPos = Math.max(completionPos, errorPos);
//                mp.reset();
//                videoView.setVideoPath(PATH_VIDEO);
//                videoView.seekTo(playerPos);
//                videoView.start();
            }
        });

        videoView.setOnErrorListener(new MediaPlayer.OnErrorListener() {
            @Override
            public boolean onError(MediaPlayer mp, int what, int extra) {
                errorPos = mp.getCurrentPosition();
                playerPos = Math.max(completionPos, errorPos);
                try {
                    mp.reset();
                    videoView.setVideoPath(PATH_VIDEO);
                    videoView.seekTo(playerPos);
                    videoView.start();
                } catch (IllegalArgumentException e) {
                    e.printStackTrace();
                } catch (IllegalStateException e) {
                    e.printStackTrace();
                } catch (Exception e) {
                    e.printStackTrace();
                }
                return true;
            }
        });


        videoView.setMediaController(mediaController);
        mediaController.setAnchorView(videoView);
        videoView.setVideoPath(PATH_VIDEO);
        videoView.start();

    }

}