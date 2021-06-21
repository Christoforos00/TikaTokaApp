package gr.aueb.tikatokaapp.View;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

import android.content.Intent;
import android.net.wifi.WifiManager;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Environment;
import android.text.format.Formatter;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.URL;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import gr.aueb.tikatokaapp.Core.AppNode;
import gr.aueb.tikatokaapp.Core.ConnectedAppNode;
import gr.aueb.tikatokaapp.R;


public class MainActivity extends AppCompatActivity {

    private static final String USER_NAME_EXTRA = "user_name_extra";
    String userName;

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


    public void onEnterClicked() {
        userName = ((EditText) findViewById(R.id.userName_text)).getText().toString();
        if (userName.length() < 3 || userName.length() > 12 || !validateName(userName)) {
            showPopUp();
            return;
        }
        AppNodeRunner run = new AppNodeRunner();
        run.execute();
        try {
            Thread.sleep(2000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        Intent intent = new Intent(MainActivity.this, MenuActivity.class);
        System.out.println(ConnectedAppNode.getAppNode());
        startActivity(intent);
    }

    public void showPopUp() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        View customLayout = getLayoutInflater().inflate(R.layout.wrong_input, null);
        builder.setView(customLayout);
        AlertDialog dialog = builder.create();
        Button OKbtn = (Button) customLayout.findViewById(R.id.OK_btn);
        OKbtn.setOnClickListener(v -> dialog.dismiss());
        dialog.show();
    }


    // ONLY ALPHANUMERICAL
    public boolean validateName(String channelName) {
        String valid = "^[a-zA-Z0-9]+$";
        Pattern pattern = Pattern.compile(valid);
        Matcher matcher = pattern.matcher(channelName);
        return matcher.matches();
    }

    private class AppNodeRunner extends AsyncTask<String, String, String> {

        @Override
        protected String doInBackground(String... strings) {
            try {
                WifiManager wm = (WifiManager) getApplicationContext().getSystemService(WIFI_SERVICE);
                String ip = Formatter.formatIpAddress(wm.getConnectionInfo().getIpAddress());
                Log.wtf("ip", ip);
                ConnectedAppNode.setAppNode(new AppNode(ip, 5000, userName, getAssets().open(String.format("brokers.txt")), Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES).getAbsolutePath()));
            } catch (IOException | InterruptedException e) {
                e.printStackTrace();
            }
            return "1";
        }


        @Override
        protected void onPostExecute(String result) {

        }
    }


}