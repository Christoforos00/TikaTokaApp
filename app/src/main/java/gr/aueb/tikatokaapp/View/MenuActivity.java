package gr.aueb.tikatokaapp.View;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.widget.EditText;

import gr.aueb.tikatokaapp.R;

public class MenuActivity extends AppCompatActivity {
    String userName;
    private static final String USER_NAME_EXTRA = "user_name_extra" ;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_menu);

        findViewById(R.id.channel_button).setOnClickListener(v -> onChannelPressed());
        userName = this.getIntent().getStringExtra(USER_NAME_EXTRA);
        Log.wtf("username" , userName);

    }


    public void onChannelPressed(){
        Intent intent = new Intent(MenuActivity.this, PublishedVideosActivity.class);
        startActivity(intent);
    }
}