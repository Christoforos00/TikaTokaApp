package gr.aueb.tikatokaapp.View.fragmentTopicList;

import android.annotation.SuppressLint;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.Arrays;

import gr.aueb.tikatokaapp.Core.ConnectedAppNode;
import gr.aueb.tikatokaapp.R;
import gr.aueb.tikatokaapp.View.fragmentTopicList.TopicListFragment.OnListFragmentInteractionListener;


public class TopicListRecyclerViewAdapter extends RecyclerView.Adapter<TopicListRecyclerViewAdapter.ViewHolder> {


    private final ArrayList<String> mValues;
    private final TopicListFragment.OnListFragmentInteractionListener mListener;

    public TopicListRecyclerViewAdapter(ArrayList<String> items, OnListFragmentInteractionListener listener) {
        mValues = items;
        mListener = listener;
    }


    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.fragment_topic_list_item, parent, false);
        return new ViewHolder(view);
    }

    @SuppressLint("ResourceAsColor")
    @Override
    public void onBindViewHolder(final ViewHolder holder, int position) {
        String currentTopic = mValues.get(position);
        holder.mItem = currentTopic;
        holder.txtTopicName.setText(currentTopic);
        holder.btnSelect.setOnClickListener(v -> {
            if (null != mListener) {
                mListener.onListFragmentInteraction(holder,  holder.mItem);
            }
        });

//        ArrayList<String> sub = new ArrayList<>(Arrays.asList("#VIRAL", "#DOGGO", "#SHIE"));
        //        if (sub.contains(currentTopic)){
        if (ConnectedAppNode.getAppNode().getSubscribedTopics().contains(currentTopic)){
            Log.wtf("selected","selected");
            holder.setSelected();
        }
    }


    @Override
    public int getItemCount() {
        return mValues.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        public final View mView;
        public final TextView txtTopicName;
        public final LinearLayout btnSelect;
        public String mItem;
        public boolean SELECTED;


        public ViewHolder(View view) {
            super(view);
            mView = view;
            txtTopicName = view.findViewById(R.id.txt_topic_name);
            btnSelect = view.findViewById(R.id.btn_select_topic);
            SELECTED = false;
        }

        public void clicked(){
            if (!SELECTED) {
                setSelected();
            } else {
                btnSelect.setBackgroundColor(mView.getContext().getResources().getColor(R.color.melon));
                btnSelect.setBackgroundResource(R.drawable.border);
                SELECTED = false;
            }
        }

        public void setSelected(){
            btnSelect.setBackgroundColor(mView.getContext().getResources().getColor(R.color.transparent_custom));
            SELECTED = true;
        }

        @Override
        public String toString() {
            return super.toString() + " '" + txtTopicName.getText() + "'";
        }
    }


}
