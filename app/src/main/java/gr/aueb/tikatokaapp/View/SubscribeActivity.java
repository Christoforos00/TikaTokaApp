package gr.aueb.tikatokaapp.View;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.util.TimeUtils;

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
    private ArrayList<String> topicList = new ArrayList<>();
    private ArrayList<String> topicsSelected = ConnectedAppNode.getAppNode().getSubscribedTopics();
    private Set<String> newSubs;
    private Set<String> newUnsubs;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        TopicRunner run = new TopicRunner();
        run.execute();
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
        Set<String> previousSubs = new HashSet<>(ConnectedAppNode.getAppNode().getSubscribedTopics());
        newUnsubs = new HashSet<>(previousSubs);
        Set<String> currentSubs = new HashSet<>(topicsSelected);
        newSubs = new HashSet<>(currentSubs);

        newUnsubs.removeAll(currentSubs);
        newSubs.removeAll(previousSubs);

        SubscribeRunner run = new SubscribeRunner();
        run.execute();

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
    }

    @Override
    public ArrayList<String> getTopicList() {
        return topicList;
    }


    private class SubscribeRunner extends AsyncTask<String, String, String> {

        @Override
        protected String doInBackground(String... strings) {
            ConnectedAppNode.getAppNode().disconnectAll(newUnsubs);
            ConnectedAppNode.getAppNode().registerAll(newSubs);
            return "1";
        }


        @Override
        protected void onPostExecute(String result) {
        }
    }


    private class TopicRunner extends AsyncTask<String, String, String> {

        @Override
        protected String doInBackground(String... strings) {
            topicList = ConnectedAppNode.getAppNode().findAllTopics();
            return "1";
        }


        @Override
        protected void onPostExecute(String result) {
        }
    }
}