package gr.aueb.tikatokaapp.View;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.widget.EditText;

import gr.aueb.tikatokaapp.R;

public class MainActivity extends AppCompatActivity {

    private static final String USER_NAME_EXTRA = "user_name_extra" ;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        findViewById(R.id.enter_button).setOnClickListener(v -> onEnterClicked());
    }



    public void onEnterClicked(){
        String userName = ((EditText) findViewById(R.id.userName_text)) .getText().toString();
        Intent intent = new Intent(MainActivity.this, MenuActivity.class);
        intent.putExtra(USER_NAME_EXTRA, userName);
        startActivity(intent);
    }


}