package gr.aueb.tikatokaapp.View.fragmentVideoList;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;
import gr.aueb.tikatokaapp.R;
import gr.aueb.tikatokaapp.View.fragmentVideoList.VideoListFragment.OnListFragmentInteractionListener;

import java.util.ArrayList;

import gr.aueb.tikatokaapp.Core.Value;

public class VideoListRecyclerViewAdapter extends RecyclerView.Adapter<VideoListRecyclerViewAdapter.ViewHolder> {

    private final ArrayList<Value> mValues;
    private final VideoListFragment.OnListFragmentInteractionListener mListener;

    public VideoListRecyclerViewAdapter(ArrayList<Value> items, OnListFragmentInteractionListener listener) {
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
        Value currentValue = mValues.get(position);
        holder.mItem = currentValue;
        holder.txtVideoName.setText(currentValue.getVideoFile().getVideoName() );
        holder.txtCreationDate.setText(currentValue.getVideoFile().getDateCreated() );
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
        public final TextView txtVideoName;
        public final TextView txtCreationDate;
        public final LinearLayout btnSelect;
        public Value mItem;


        public ViewHolder(View view) {
            super(view);
            mView = view;
            txtVideoName = view.findViewById(R.id.txt_video_name);
            txtCreationDate = view.findViewById(R.id.txt_video_date);
            btnSelect = view.findViewById(R.id.btn_select_video);
        }

        @Override
        public String toString() {
            return super.toString() + " '" + txtVideoName.getText() + "'";
        }
    }



}
