package application.pascoe.com.ci301.utility;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;

import org.w3c.dom.Text;

import application.pascoe.com.ci301.R;
import application.pascoe.com.ci301.constants.Constants;
import application.pascoe.com.ci301.sqlite.SQLDatabase;

public class MainActivity extends AppCompatActivity {

    private static final String TAG = "MainActivity";
    private boolean coarseLocationPermissionGranted = false;
    private boolean fineLocationPermissionGranted = false;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        SQLDatabase db = new SQLDatabase(this);

        checkPermissions();

        final EditText txt_passwordConfirm = findViewById(R.id.txt_passwordConfirm);
        final CheckBox cb_createAccount = findViewById(R.id.cb_createAccount);
        final Button startButton = findViewById(R.id.btn_start);
        Button quitButton = findViewById(R.id.btn_quit);

        cb_createAccount.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if(cb_createAccount.isChecked()){
                    txt_passwordConfirm.setVisibility(txt_passwordConfirm.VISIBLE);
                    startButton.setText("CREATE ACCOUNT");
                }else{
                    txt_passwordConfirm.setVisibility(txt_passwordConfirm.INVISIBLE);
                    startButton.setText("LOGIN");
                }
            }
        });

        startButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(fineLocationPermissionGranted && coarseLocationPermissionGranted) {
                    Intent intent = new Intent(MainActivity.this, MapActivity.class);
                    startActivity(intent);
                }
            }
        });

        quitButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                System.exit(0);
            }
        });
    }

    @Override
    protected void onResume() {
        super.onResume();
        checkPermissions();
    }

    private void checkPermissions() {
        if (ContextCompat.checkSelfPermission(MainActivity.this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                ActivityCompat.requestPermissions(MainActivity.this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, Constants.PERMISSION_REQUEST_ACCESS_FINE_LOCATION);
        } else {
            fineLocationPermissionGranted = true;
        }

        if (ContextCompat.checkSelfPermission(MainActivity.this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(MainActivity.this, new String[]{Manifest.permission.ACCESS_COARSE_LOCATION}, Constants.PERMISSION_REQUEST_ACCESS_COARSE_LOCATION);
        } else {
            coarseLocationPermissionGranted = true;
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        switch (requestCode) {
            case Constants.PERMISSION_REQUEST_ACCESS_FINE_LOCATION: {
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    fineLocationPermissionGranted = true;
                    Log.d(TAG, "onRequestPermissionsResult: PERMISSIONS GRANTED");
                } else {
                    fineLocationPermissionGranted = false;
                    Log.d(TAG, "onRequestPermissionsResult: PERMISSIONS DENIED");
                    // SHOW DIALOG & REPEAT REQUEST PERMISSIONS
                }

            }
            case Constants.PERMISSION_REQUEST_ACCESS_COARSE_LOCATION: {
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    coarseLocationPermissionGranted = true;
                    Log.d(TAG, "onRequestPermissionsResult: PERMISSIONS GRANTED");
                } else {
                    coarseLocationPermissionGranted = false;
                    Log.d(TAG, "onRequestPermissionsResult: PERMISSIONS DENIED");
                    // SHOW DIALOG & REPEAT REQUEST PERMISSIONS
                }
            }
        }
    }
}
