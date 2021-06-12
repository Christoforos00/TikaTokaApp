package gr.aueb.tikatokaapp.View;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

import gr.aueb.tikatokaapp.Core.ConnectedAppNode;
import gr.aueb.tikatokaapp.R;
import gr.aueb.tikatokaapp.View.fragmentTopicList.TopicListFragment;
import gr.aueb.tikatokaapp.View.fragmentTopicList.TopicListRecyclerViewAdapter.ViewHolder;

public class SubscribeActivity extends AppCompatActivity implements TopicListFragment.OnListFragmentInteractionListener {

    private static boolean isSelected = false;
    private ArrayList<String> topicsSelected = new ArrayList<>(Arrays.asList("#VIRAL", "#DOGGO", "#SHIE"));
//    private ArrayList<String> topicsSelected = ConnectedAppNode.getAppNode().getSubscribedTopics();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_subscribe);

        findViewById(R.id.button).setOnClickListener(v -> onDone());

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

    private void onDone() {
//        Set<String> previousSubs = new HashSet<>(ConnectedAppNode.getAppNode().getSubscribedTopics());
        Set<String> previousSubs = new HashSet<>(new ArrayList<>(Arrays.asList("#VIRAL", "#DOGGO", "#SHIE")));

        Set<String> newUnsubs = new HashSet<>(previousSubs);
        Set<String> currentSubs = new HashSet<>(topicsSelected);
        Set<String> newSubs = new HashSet<>(currentSubs);

        newUnsubs.removeAll(currentSubs);
        newSubs.removeAll(previousSubs);

        Log.wtf("subs",newSubs.toString());
        Log.wtf("unsubs",newUnsubs.toString());
        Intent intent = new Intent(SubscribeActivity.this, MenuActivity.class);
        startActivity(intent);
    }

    @Override
    public void onListFragmentInteraction(ViewHolder item, String topic) {
        item.clicked();
        if (item.SELECTED)
            topicsSelected.add(topic);
        else
            topicsSelected.remove(topic);
        Log.wtf("sel",topicsSelected.toString());
    }

    //TODO return available topics ( from online pubs )
    @Override
    public ArrayList<String> getTopicList() {
//        return ConnectedAppNode.getAppNode().findAllTopics();
        return new ArrayList<>(Arrays.asList("#VIRAL", "#DOG", "#DOGGO", "#CATTO", "#BOONK", "#GANG", "#SHIE"));
    }

}