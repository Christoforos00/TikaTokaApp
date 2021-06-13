package gr.aueb.tikatokaapp.View;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.media.MediaMetadataRetriever;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

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
import gr.aueb.tikatokaapp.View.fragmentVideoList.VideoListFragment;


public class UploadVideosActivity extends AppCompatActivity implements VideoListFragment.OnListFragmentInteractionListener {

    public static final int CAMERA_PERMISSION_CODE = 100;
    public static final int RECORD_CODE = 1;
    public static final int GALLERY_CODE = 2;
    private String OLD_VIDEO_PATH;
    private String NEW_VIDEO_PATH;
    private String VIDEO_NAME;
    private String HASHTAGS;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_upload_videos);

        findViewById(R.id.rec_button).setOnClickListener(v -> onRec());
        findViewById(R.id.gallery_button).setOnClickListener(v -> onGallery());

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
        startActivityForResult(intent, RECORD_CODE);
    }

    public void onGallery(){
        Intent intent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        intent.setType("video/*");
        startActivityForResult(intent, GALLERY_CODE);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == RESULT_OK && requestCode == RECORD_CODE) {
            Uri vid = data.getData();
            OLD_VIDEO_PATH = getRealPathFromURI(vid);
            NEW_VIDEO_PATH = ConnectedAppNode.getAppNode().getPubDir();
            showPopUp(true);
        }else if (resultCode == RESULT_OK && requestCode == GALLERY_CODE) {
            Uri vid = data.getData();
            OLD_VIDEO_PATH = getRealPathFromURI(vid);
            NEW_VIDEO_PATH = ConnectedAppNode.getAppNode().getPubDir();
            showPopUp(false);
        }
    }

    public void showPopUp(Boolean delete) {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        View customLayout = getLayoutInflater().inflate(R.layout.enter_hashtags_popup, null);
        builder.setView(customLayout);
        AlertDialog dialog = builder.create();

        Button uploadBtn = (Button) customLayout.findViewById(R.id.enter_hashtags_btn);
        EditText videoName = (EditText) customLayout.findViewById(R.id.videoName);
        EditText hashtags = (EditText) customLayout.findViewById(R.id.hashtags);
        uploadBtn.setOnClickListener(v -> {
            VIDEO_NAME = videoName.getText().toString() + ".mp4";
            HASHTAGS = hashtags.getText().toString();
            uploadRecordedVideo(delete);
            dialog.dismiss();
        });

        dialog.show();
    }


    private void uploadRecordedVideo(Boolean delete) {
        try {
            transferVideo(new File(OLD_VIDEO_PATH), new File(NEW_VIDEO_PATH + "/videos/" + VIDEO_NAME));
        } catch (IOException e) {
            e.printStackTrace();
        }
        if (delete)
            deleteRecursive(new File(OLD_VIDEO_PATH));

        UploadRunner run = new UploadRunner();
        run.execute();
        Intent intent = new Intent(UploadVideosActivity.this, PublishedVideosActivity.class);
        startActivity(intent);
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


    private class UploadRunner extends AsyncTask<String, String, String> {

        @Override
        protected String doInBackground(String... strings) {
            ConnectedAppNode.getAppNode().uploadVideo(VIDEO_NAME, HASHTAGS);
            return "1";
        }


        @Override
        protected void onPostExecute(String result) {
        }
    }


}