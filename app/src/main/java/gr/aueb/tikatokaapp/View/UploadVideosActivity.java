package gr.aueb.tikatokaapp.View;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.widget.ImageButton;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.util.ArrayList;
import gr.aueb.tikatokaapp.Core.Value;
import gr.aueb.tikatokaapp.R;
import gr.aueb.tikatokaapp.View.fragmentList.VideoListFragment;


public class UploadVideosActivity extends AppCompatActivity implements VideoListFragment.OnListFragmentInteractionListener{

    public static final int CAMERA_PERMISSION_CODE = 100;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_upload_videos);

        ((ImageButton) findViewById(R.id.rec_button)).setOnClickListener(v -> onRec() );

        if (findViewById(R.id.fragment_container) != null) {

            if (savedInstanceState != null) {
                return;
            }

            VideoListFragment videoListFragment = VideoListFragment.newInstance(1);
            getSupportFragmentManager().beginTransaction()
                    .add(R.id.fragment_container, videoListFragment)
                    .commit();
        }

        getCameraPermission();
    }


    @Override
    public void onListFragmentInteraction(Value item) {
        //can delete
    }

    @Override
    public ArrayList<Value> getVideoList() {

        return new ArrayList<Value>();
    }

    public void onRec(){
        Intent intent = new Intent( MediaStore.ACTION_VIDEO_CAPTURE);
//        intent.putExtra(MediaStore.EXTRA_OUTPUT, Environment.getExternalStorageDirectory());
        intent.putExtra(MediaStore.EXTRA_DURATION_LIMIT , 60);
        startActivityForResult(intent, 1);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if ( resultCode == RESULT_OK && requestCode ==1){

        }

    }

    public void getCameraPermission(){
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.CAMERA) == PackageManager.PERMISSION_DENIED){
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.CAMERA} , CAMERA_PERMISSION_CODE  );
        }
    }
}