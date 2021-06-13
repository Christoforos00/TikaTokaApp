package gr.aueb.tikatokaapp.View.fragmentVideoList;

import android.media.MediaMetadataRetriever;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import gr.aueb.tikatokaapp.Core.ConnectedAppNode;
import gr.aueb.tikatokaapp.R;
import gr.aueb.tikatokaapp.View.PublishedVideosActivity;
import gr.aueb.tikatokaapp.View.fragmentVideoList.VideoListFragment.OnListFragmentInteractionListener;

import java.io.File;
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
        holder.txtVideoName.setText(currentValue.getVideoFile().getVideoName());
        holder.txtCreationDate.setText(currentValue.getVideoFile().getDateCreated());
        holder.setThumbnail();
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
        private final ImageView imageView;
        public Value mItem;


        public ViewHolder(View view) {
            super(view);
            mView = view;
            txtVideoName = view.findViewById(R.id.txt_video_name);
            txtCreationDate = view.findViewById(R.id.txt_video_date_duration);
            btnSelect = view.findViewById(R.id.btn_select_video);
            imageView = view.findViewById(R.id.video_thumbnail);
        }

        public void setThumbnail(){
            MediaMetadataRetriever retriever = new MediaMetadataRetriever();
            if (mView.getContext() instanceof PublishedVideosActivity ) {
                retriever.setDataSource(ConnectedAppNode.getAppNode().getPubDir() + "/videos/" +   mItem.getName() );
                imageView.setImageBitmap( retriever.getFrameAtTime() );
            }else {
                retriever.setDataSource(ConnectedAppNode.getAppNode().getSubDir() + "/videos/" +   mItem.getName() );
                imageView.setImageBitmap( retriever.getFrameAtTime() );
            }
        }


        @Override
        public String toString() {
            return super.toString() + " '" + txtVideoName.getText() + "'";
        }
    }


}
