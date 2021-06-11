package gr.aueb.tikatokaapp.View.fragmentTopicList;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;
import androidx.recyclerview.widget.RecyclerView;
import java.util.ArrayList;
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
                .inflate(R.layout.fragment_video_list_item, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(final ViewHolder holder, int position) {
        String currentTopic = mValues.get(position);
        holder.mItem = currentTopic;
        holder.txtTopicName.setText(currentTopic);
        holder.btnSelect.setOnClickListener((View.OnClickListener) v -> {
            if (null != mListener) {
                mListener.onListFragmentInteraction(holder.mItem);
            }
        });
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


        public ViewHolder(View view) {
            super(view);
            mView = view;
            txtTopicName = view.findViewById(R.id.txt_topic_name);
            btnSelect = view.findViewById(R.id.btn_select_topic);
        }

        @Override
        public String toString() {
            return super.toString() + " '" + txtTopicName.getText() + "'";
        }
    }




}
