package gr.aueb.tikatokaapp.View;

import android.content.Intent;
import android.media.MediaMetadataRetriever;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.MediaController;
import android.widget.VideoView;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.FragmentManager;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.Scanner;
import java.util.concurrent.TimeUnit;

import gr.aueb.tikatokaapp.Core.AppNode;
import gr.aueb.tikatokaapp.Core.ConnectedAppNode;
import gr.aueb.tikatokaapp.Core.Value;
import gr.aueb.tikatokaapp.Core.VideoFile;
import gr.aueb.tikatokaapp.R;
import gr.aueb.tikatokaapp.View.fragmentVideoList.VideoListFragment;

public class FeedActivity extends AppCompatActivity implements VideoListFragment.OnListFragmentInteractionListener, MediaPlayer.OnPreparedListener {

    private VideoView mVideoView = null;
    MediaController mediaController = null;
    private static final String VIDEO_ΝΑΜΕ = "video_name_extra";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_feed);

        HandleRefresh action = new HandleRefresh();
        action.start();

        if (findViewById(R.id.fragment_container) != null) {

            if (savedInstanceState != null) {
                return;
            }
            VideoListFragment videoListFragment = VideoListFragment.newInstance(1);
            getSupportFragmentManager().beginTransaction()
                    .add(R.id.fragment_container, videoListFragment)
                    .commit();
        }
    }


    @Override
    public void onListFragmentInteraction(Value item) {
        String pathVideo = ConnectedAppNode.getAppNode().getSubDir() + "/videos/" + item.getName();
        Intent intent = new Intent(FeedActivity.this, VideoPlayerActivity.class);
        intent.putExtra("PATH", pathVideo);
        intent.putExtra(VIDEO_ΝΑΜΕ, item.getName());
        startActivity(intent);
    }

    @Override
    public ArrayList<Value> getVideoList() {
        ArrayList<Value> videos = new ArrayList<>();
        MediaMetadataRetriever retriever;

        Scanner scanner = null;
        try {
            scanner = new Scanner(new FileReader(ConnectedAppNode.getAppNode().getSubDir() + "/topics.txt"));
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }

        File f;
        while (scanner.hasNextLine()) {
            String[] parts = scanner.nextLine().split(":");
            f = new File(ConnectedAppNode.getAppNode().getSubDir() + "/videos/" + parts[0]);
            Log.wtf("PATH I READ FROM MOBILE", f.getAbsolutePath());
            retriever = new MediaMetadataRetriever();
            retriever.setDataSource(f.getAbsolutePath());

            VideoFile videoFile = new VideoFile(f.getName(), parts[1], String.valueOf(retriever.METADATA_KEY_DATE), String.valueOf(retriever.METADATA_KEY_DURATION)
                    , String.valueOf(retriever.METADATA_KEY_CAPTURE_FRAMERATE), String.valueOf(retriever.METADATA_KEY_IMAGE_HEIGHT), String.valueOf(retriever.METADATA_KEY_IMAGE_WIDTH), null);

            videos.add(new Value(videoFile));
        }
        return videos;
    }


    public void onAddVideo() {
        Intent intent = new Intent(FeedActivity.this, UploadVideosActivity.class);
        startActivity(intent);
    }


    @Override
    public void onPrepared(MediaPlayer mp) {
        mVideoView.start();
        mediaController.show(500);
    }


    public class HandleRefresh extends Thread {
        public HandleRefresh() {
        }

        public void run() {
            try {
                while (!isInterrupted()) {
                    Thread.sleep(10000);
                    runOnUiThread(() -> {
                        Log.wtf("feed", "feed refreshhhhhh");
                        VideoListFragment videoListFragment = VideoListFragment.newInstance(1);
                        FragmentManager fragmentManager = getSupportFragmentManager();
                        fragmentManager.beginTransaction().replace(R.id.fragment_container, videoListFragment).commit();
                    });
                }
            } catch (InterruptedException e) {
            }
        }
    }
}
