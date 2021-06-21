package gr.aueb.tikatokaapp.View;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.MediaController;

import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.util.ArrayList;

import gr.aueb.tikatokaapp.Core.ConnectedAppNode;
import gr.aueb.tikatokaapp.Core.Value;
import gr.aueb.tikatokaapp.R;
import gr.aueb.tikatokaapp.View.fragmentVideoList.VideoListFragment;

public class PublishedVideosActivity extends AppCompatActivity implements VideoListFragment.OnListFragmentInteractionListener {


    private static final String VIDEO_ΝΑΜΕ = "video_name_extra";
    private static final String PATH = "PATH";
    private AlertDialog POPUP_ACTION;
    private Value currentItem;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_published_videos);
        ((FloatingActionButton) findViewById(R.id.add_video_button)).setOnClickListener(v -> onAddVideo());


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
    public void onBackPressed() {
        super.onBackPressed();
        Intent intent = new Intent(this, MenuActivity.class);
        startActivity(intent);
    }


    @Override
    public void onListFragmentInteraction(Value item) {
        String pathVideo = ConnectedAppNode.getAppNode().getPubDir() + "/videos/" + item.getName();
        POPUP_ACTION = showPopUp(pathVideo, item);
        POPUP_ACTION.show();
    }

    @Override
    public ArrayList<Value> getVideoList() {
        ArrayList<Value> vals = ConnectedAppNode.getAppNode().getVideos(ConnectedAppNode.getAppNode().getName());
        if (vals == null)
            return new ArrayList<Value>();
        return vals;
    }

    public void onAddVideo() {
        Intent intent = new Intent(PublishedVideosActivity.this, UploadVideosActivity.class);
        startActivity(intent);
    }


    public AlertDialog showPopUp(String pathVideo, Value item) {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        View customLayout = getLayoutInflater().inflate(R.layout.publisher_action, null);
        builder.setView(customLayout);
        AlertDialog dialog = builder.create();

        FloatingActionButton removeBtn = (FloatingActionButton) customLayout.findViewById(R.id.remove_video_button);
        FloatingActionButton playBtn = (FloatingActionButton) customLayout.findViewById(R.id.play_video_button);

        removeBtn.setOnClickListener(v -> onDeleteVideoAction(item));
        playBtn.setOnClickListener(v -> onPlayVideoAction(pathVideo, item));
        return dialog;
    }

    public void onDeleteVideoAction(Value item) {
        currentItem = item;
        DeleteVideoRunner runner = new DeleteVideoRunner();
        runner.execute();

        POPUP_ACTION.dismiss();
        POPUP_ACTION = null;
        this.recreate();
    }

    public void onPlayVideoAction(String path, Value item) {
        Intent intent = new Intent(this, VideoPlayerActivity.class);
        intent.putExtra(VIDEO_ΝΑΜΕ, item.getName());
        intent.putExtra(PATH, path);
        POPUP_ACTION.dismiss();
        POPUP_ACTION = null;
        startActivity(intent);
    }


    private class DeleteVideoRunner extends AsyncTask<String, String, String> {

        @Override
        protected String doInBackground(String... strings) {
            ConnectedAppNode.getAppNode().deleteVideo(currentItem.getVideoFile().getVideoName());
            return "1";
        }

        @Override
        protected void onPostExecute(String result) {
        }

    }

}