package gr.aueb.tikatokaapp.View;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Bundle;
import android.widget.LinearLayout;

import java.util.ArrayList;
import java.util.Arrays;

import gr.aueb.tikatokaapp.R;
import gr.aueb.tikatokaapp.View.fragmentTopicList.TopicListFragment;
import gr.aueb.tikatokaapp.View.fragmentTopicList.TopicListRecyclerViewAdapter;
import gr.aueb.tikatokaapp.View.fragmentVideoList.VideoListFragment;
import gr.aueb.tikatokaapp.View.fragmentTopicList.TopicListRecyclerViewAdapter.ViewHolder;

public class SubscribeActivity extends AppCompatActivity implements TopicListFragment.OnListFragmentInteractionListener {

    private static boolean isSelected = false;
    private ArrayList<String> topicsSelected = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_subscribe);

        if (findViewById(R.id.fragment_container) != null) {

            if (savedInstanceState != null) {
                return;
            }

            TopicListFragment topicListFragment = TopicListFragment.newInstance(1);
            getSupportFragmentManager().beginTransaction()
                    .add(R.id.fragment_container, topicListFragment)
                    .commit();
        }
    }

    @Override
    public void onListFragmentInteraction(ViewHolder item) {
        if (!item.SELECTED) {
            item.btnSelect.setBackgroundColor(getResources().getColor(R.color.transparent_custom));
            item.SELECTED = true;
        } else {
            item.btnSelect.setBackgroundColor(getResources().getColor(R.color.melon));
            item.btnSelect.setBackgroundResource(R.drawable.border);
            item.SELECTED = false;
        }

    }

    //TODO return available topics ( from online pubs )
    @Override
    public ArrayList<String> getTopicList() {
        return new ArrayList<>(Arrays.asList("#VIRAL", "#DOG", "#VIRAL", "#VIRAL", "#VIRAL", "#VIRAL", "#VIRAL"));
    }

}