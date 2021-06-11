package gr.aueb.tikatokaapp.View;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.os.Environment;
import android.util.Log;
import android.widget.EditText;

import java.io.File;

import gr.aueb.tikatokaapp.R;

public class MenuActivity extends AppCompatActivity {
    String userName;
    private static final String USER_NAME_EXTRA = "user_name_extra" ;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_menu);

        findViewById(R.id.channel_button).setOnClickListener(v -> onChannelPressed());

//        Log.wtf("ssssssss", String.valueOf(Environment.isExternalStorageManager()) );
        ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.CAMERA} , 1  );

        Log.wtf("perrr", String.valueOf(checkSelfPermission(android.Manifest.permission.WRITE_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED));
        Log.wtf("p", Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS).getAbsolutePath());
        Log.wtf("pp", Environment.getRootDirectory().getAbsolutePath());

        File[] files = new File(Environment.getExternalStorageDirectory().getAbsolutePath()).listFiles();
        for (File f:files)
            Log.wtf("ggggg", f.getAbsolutePath());

    }


    public void onChannelPressed(){
        Intent intent = new Intent(MenuActivity.this, PublishedVideosActivity.class);
        startActivity(intent);
    }
}