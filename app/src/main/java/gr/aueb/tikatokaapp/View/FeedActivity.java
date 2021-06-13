package gr.aueb.tikatokaapp.View;

import android.content.Intent;
import android.media.MediaMetadataRetriever;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.MediaController;
import android.widget.VideoView;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.util.ArrayList;
import java.util.Scanner;

import gr.aueb.tikatokaapp.Core.ConnectedAppNode;
import gr.aueb.tikatokaapp.Core.Value;
import gr.aueb.tikatokaapp.Core.VideoFile;
import gr.aueb.tikatokaapp.R;
import gr.aueb.tikatokaapp.View.fragmentVideoList.VideoListFragment;

public class FeedActivity extends AppCompatActivity implements VideoListFragment.OnListFragmentInteractionListener {


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_feed);

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
        showPopUp(pathVideo);

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
            retriever = new MediaMetadataRetriever();
            retriever.setDataSource(f.getAbsolutePath());

            VideoFile videoFile = new VideoFile(f.getName(), parts[1], String.valueOf(retriever.METADATA_KEY_DATE), String.valueOf(retriever.METADATA_KEY_DURATION)
                    , String.valueOf(retriever.METADATA_KEY_CAPTURE_FRAMERATE), String.valueOf(retriever.METADATA_KEY_IMAGE_HEIGHT), String.valueOf(retriever.METADATA_KEY_IMAGE_WIDTH), null);

            videos.add(new Value(videoFile));
        }
        return videos;
    }

    public void showPopUp(String pathVideo) {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        View customLayout = getLayoutInflater().inflate(R.layout.video_player, null);
        builder.setView(customLayout);
        AlertDialog dialog = builder.create();

        VideoView videoPlayer = (VideoView) customLayout.findViewById(R.id.video_playing);
        videoPlayer.setVideoPath(pathVideo);
        MediaController mediaController = new MediaController(this);
        mediaController.setAnchorView(videoPlayer);
        videoPlayer.setMediaController(mediaController);
        dialog.show();
    }

    public void onAddVideo() {
        Intent intent = new Intent(FeedActivity.this, UploadVideosActivity.class);
        startActivity(intent);
    }


}
