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
import android.widget.TextView;

import java.io.File;

import gr.aueb.tikatokaapp.Core.ConnectedAppNode;
import gr.aueb.tikatokaapp.R;

public class MenuActivity extends AppCompatActivity {
    private static final String USER_NAME_EXTRA = "user_name_extra";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_menu);
        TextView title = (TextView) findViewById(R.id.channel_name_title);
        title.setText(ConnectedAppNode.getAppNode().getName());
        findViewById(R.id.channel_button).setOnClickListener(v -> onChannelPressed());
        findViewById(R.id.feed_button).setOnClickListener(v -> onFeedPressed());
        findViewById(R.id.subs_button).setOnClickListener(v -> onSubscriptionsPressed());
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        Intent intent = new Intent(this, MainActivity.class);
        ConnectedAppNode.clearAppNode();
        startActivity(intent);

    }

    public void onChannelPressed() {
        Intent intent = new Intent(MenuActivity.this, PublishedVideosActivity.class);
        startActivity(intent);
    }

    public void onFeedPressed() {
        Intent intent = new Intent(MenuActivity.this, FeedActivity.class);
        startActivity(intent);
    }

    public void onSubscriptionsPressed() {
        Intent intent = new Intent(MenuActivity.this, SubscribeActivity.class);
        startActivity(intent);
    }
}