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
    private static final String USER_NAME_EXTRA = "user_name_extra";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_menu);

        findViewById(R.id.channel_button).setOnClickListener(v -> onChannelPressed());
        findViewById(R.id.feed_button).setOnClickListener(v -> onFeedPressed());
        findViewById(R.id.subs_button).setOnClickListener(v -> onSubscriptionsPressed());
    }


    public void onChannelPressed() {
        Intent intent = new Intent(MenuActivity.this, PublishedVideosActivity.class);
        startActivity(intent);
    }

    public void onFeedPressed() {
        Intent intent = new Intent(MenuActivity.this, PublishedVideosActivity.class);
        startActivity(intent);
    }

    public void onSubscriptionsPressed() {
        Intent intent = new Intent(MenuActivity.this, SubscribeActivity.class);
        startActivity(intent);
    }
}