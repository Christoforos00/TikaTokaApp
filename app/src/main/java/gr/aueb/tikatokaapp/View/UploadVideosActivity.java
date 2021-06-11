package gr.aueb.tikatokaapp.View;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.core.content.FileProvider;

import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.ArrayList;

import gr.aueb.tikatokaapp.Core.ConnectedAppNode;
import gr.aueb.tikatokaapp.Core.Value;
import gr.aueb.tikatokaapp.R;
import gr.aueb.tikatokaapp.View.fragmentList.VideoListFragment;


public class UploadVideosActivity extends AppCompatActivity implements VideoListFragment.OnListFragmentInteractionListener, View.OnClickListener {

    public static final int CAMERA_PERMISSION_CODE = 100;
    private String VIDEO_NAME;
    private String HASHTAGS;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_upload_videos);

        ((ImageButton) findViewById(R.id.rec_button)).setOnClickListener(v -> onRec());

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

    public void onRec() {
        Intent intent = new Intent(MediaStore.ACTION_VIDEO_CAPTURE);
        intent.putExtra(MediaStore.EXTRA_DURATION_LIMIT, 60);
        startActivityForResult(intent, 1);
    }

    private String videoPath = "";

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == RESULT_OK && requestCode == 1) {

            Uri vid = data.getData();
            videoPath = getRealPathFromURI(vid);
            String newPath = ConnectedAppNode.getAppNode().getPubDir();
            try {
                transferVideo(new File(videoPath), new File(newPath + "/videos/test_video.mp4"));
            } catch (IOException e) {
                e.printStackTrace();
            }
            deleteRecursive(new File(videoPath));
            showPopUp();
        }


    }

    public void showPopUp() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        View customLayout = getLayoutInflater().inflate(R.layout.enter_hashtags_popup, null);
        builder.setView(customLayout);
        AlertDialog dialog = builder.create();

        Button uploadBtn = (Button) customLayout.findViewById(R.id.enter_hashtags_btn);
        EditText videoName = (EditText) customLayout.findViewById(R.id.videoName);
        EditText hashtags = (EditText) customLayout.findViewById(R.id.hashtags);

        VIDEO_NAME = videoName.getText().toString();
        HASHTAGS = hashtags.getText().toString();

        uploadBtn.setOnClickListener(this);

        dialog.show();
    }

    private void uploadRecordedVideo() {

    }


    public String getRealPathFromURI(Uri contentUri) {
        String[] proj = {MediaStore.Images.Media.DATA};
        Cursor cursor = managedQuery(contentUri, proj, null, null, null);
        int column_index = cursor.getColumnIndexOrThrow(MediaStore.Images.Media.DATA);
        cursor.moveToFirst();
        return cursor.getString(column_index);
    }

    public static void transferVideo(File sourceLocation, File targetLocation)
            throws IOException {

        if (sourceLocation.isDirectory()) {
            if (!targetLocation.exists()) {
                targetLocation.mkdir();
            }

            String[] children = sourceLocation.list();
            for (int i = 0; i < sourceLocation.listFiles().length; i++) {

                transferVideo(new File(sourceLocation, children[i]),
                        new File(targetLocation, children[i]));
            }
        } else {

            InputStream in = new FileInputStream(sourceLocation);

            OutputStream out = new FileOutputStream(targetLocation);

            // Copy the bits from instream to outstream
            byte[] buf = new byte[1024];
            int len;
            while ((len = in.read(buf)) > 0) {
                out.write(buf, 0, len);
            }
            in.close();
            out.close();
        }

    }

    public static void deleteRecursive(File fileOrDirectory) {

        if (fileOrDirectory.isDirectory())
            for (File child : fileOrDirectory.listFiles())
                deleteRecursive(child);

        fileOrDirectory.delete();

    }

    public void getCameraPermission() {
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.CAMERA) == PackageManager.PERMISSION_DENIED) {
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.CAMERA}, CAMERA_PERMISSION_CODE);
        }
    }

    @Override
    public void onClick(View v) {
        if (v.getId() == R.id.enter_hashtags_btn) {
            uploadRecordedVideo();
        }
    }
}