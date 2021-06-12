package gr.aueb.tikatokaapp.View;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

import android.content.Intent;
import android.net.wifi.WifiManager;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Environment;
import android.text.format.Formatter;
import android.util.Log;
import android.widget.EditText;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.URL;

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
        AppNodeRunner run = new AppNodeRunner();
        run.execute();
        try {
            Thread.sleep(3000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        Intent intent = new Intent(MainActivity.this, MenuActivity.class);
        startActivity(intent);
    }


    public static String getIp() {
        BufferedReader in = null;
        String ip;
        try {
            URL whatismyip = new URL("http://checkip.amazonaws.com");
            in = new BufferedReader(new InputStreamReader(
                    whatismyip.openStream()));
            ip = in.readLine();
            return ip;
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            if (in != null) {
                try {
                    in.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
        return "";
    }

    private class AppNodeRunner extends AsyncTask<String, String, String> {

        @Override
        protected String doInBackground(String... strings) {
            try {
                WifiManager wm = (WifiManager) getApplicationContext().getSystemService(WIFI_SERVICE);
                String ip = Formatter.formatIpAddress(wm.getConnectionInfo().getIpAddress());
                Log.wtf("ippp", ip);
                ConnectedAppNode.setAppNode(new AppNode(ip, 5000, userName, getAssets().open(String.format("brokers.txt")), Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES).getAbsolutePath()));
            } catch (IOException | InterruptedException e) {
                e.printStackTrace();
            }
            Log.wtf("SET CONNECTED APPNODE ", ConnectedAppNode.getAppNode().getName());
            return "1";
        }


        @Override
        protected void onPostExecute(String result) {

        }
    }


}