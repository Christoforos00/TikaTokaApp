package gr.aueb.tikatokaapp.View;

import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;
import java.util.ArrayList;
import gr.aueb.tikatokaapp.Core.Value;
import gr.aueb.tikatokaapp.R;
import gr.aueb.tikatokaapp.View.fragmentList.VideoListFragment;

public class PublishedVideosActivity extends AppCompatActivity implements VideoListFragment.OnListFragmentInteractionListener{

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_published_videos);

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
        //can delete
    }

    @Override
    public ArrayList<Value> getVideoList() {

        return new ArrayList<Value>();
    }




}