package gr.aueb.tikatokaapp.View;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.media.MediaPlayer;
import android.os.Bundle;
import android.provider.MediaStore;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.widget.MediaController;
import android.widget.VideoView;

import java.io.File;
import java.io.FileDescriptor;
import java.io.FileInputStream;
import java.io.IOException;

import gr.aueb.tikatokaapp.R;

public class VideoPlayerActivity extends AppCompatActivity implements MediaPlayer.OnPreparedListener, SurfaceHolder.Callback{
    int completionPos , errorPos , playerPos;
    private SurfaceView surfaceView;
    private SurfaceHolder surfaceHolder;
    private MediaPlayer mediaPlayer;
    String extra;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_video_player);
        extra = this.getIntent().getStringExtra("PATH");

        surfaceView = findViewById(R.id.videoViewPlayer);
        surfaceHolder = surfaceView.getHolder();
        surfaceHolder.addCallback(VideoPlayerActivity.this);

    }

    @Override
    public void surfaceCreated(@NonNull SurfaceHolder holder) {
        System.out.println("aaa");
        mediaPlayer = new MediaPlayer();
        mediaPlayer.setDisplay(surfaceHolder);

        try {
            mediaPlayer.setDataSource( getFD(new File(extra))  );
            mediaPlayer.prepare();
            mediaPlayer.setOnPreparedListener(VideoPlayerActivity.this);

        } catch (IOException e) {
            e.printStackTrace();
        }



    }

    @Override
    public void surfaceChanged(@NonNull SurfaceHolder holder, int format, int width, int height) {

    }

    @Override
    public void surfaceDestroyed(@NonNull SurfaceHolder holder) {

    }


    @Override
    protected void onPause() {
        super.onPause();
        releaseMediaPlayer();
    }

    private void releaseMediaPlayer() {
        if (mediaPlayer!= null){
            mediaPlayer.release();
            mediaPlayer = null;
        }

    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        releaseMediaPlayer();
    }


    public FileDescriptor getFD(File file){
        FileDescriptor fd = null;
        try {
            FileInputStream fileInputStream = new FileInputStream(file);
            fd = fileInputStream.getFD();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return fd;
    }

    @Override
    public void onPrepared(MediaPlayer mp) {
        mediaPlayer.start();
    }
}