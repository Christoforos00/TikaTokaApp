package gr.aueb.tikatokaapp.View;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import android.content.Intent;
import android.net.wifi.WifiManager;
import android.os.Bundle;
import android.os.Environment;
import android.text.format.Formatter;
import android.util.Log;
import android.widget.EditText;
import java.io.IOException;
import gr.aueb.tikatokaapp.Core.AppNode;
import gr.aueb.tikatokaapp.Core.ConnectedAppNode;
import gr.aueb.tikatokaapp.R;


public class MainActivity extends AppCompatActivity {

    private static final String USER_NAME_EXTRA = "user_name_extra" ;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        findViewById(R.id.enter_button).setOnClickListener(v -> onEnterClicked());

        String[] PERMISSIONS = {
                android.Manifest.permission.READ_EXTERNAL_STORAGE,
                android.Manifest.permission.WRITE_EXTERNAL_STORAGE,
                android.Manifest.permission.MANAGE_DOCUMENTS,
                android.Manifest.permission.CAMERA,
                android.Manifest.permission.MANAGE_EXTERNAL_STORAGE
        };
        ActivityCompat.requestPermissions(this, PERMISSIONS, 1);
    }


    public void onEnterClicked(){
        String userName = ((EditText) findViewById(R.id.userName_text)) .getText().toString();
        try {
            WifiManager wm = (WifiManager) getApplicationContext().getSystemService(WIFI_SERVICE);
            String ip = Formatter.formatIpAddress(wm.getConnectionInfo().getIpAddress());
            Log.wtf("ippp",ip);
            ConnectedAppNode.setAppNode( new AppNode( ip,5000, userName, getAssets().open(String.format("brokers.txt")), Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES).getAbsolutePath() ));
        } catch (IOException | InterruptedException e) {
            e.printStackTrace();
        }
        Intent intent = new Intent(MainActivity.this, MenuActivity.class);
        startActivity(intent);
    }


}