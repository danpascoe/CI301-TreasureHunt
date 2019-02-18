package application.pascoe.com.ci301.utility;

import android.Manifest;
import android.content.Context;
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
import android.widget.TextView;
import android.widget.Toast;

import application.pascoe.com.ci301.R;
import application.pascoe.com.ci301.constants.Constants;

public class MainActivity extends AppCompatActivity  {


    private static final String TAG = "MainActivity";
    private boolean coarseLocationPermissionGranted = false;
    private boolean fineLocationPermissionGranted = false;
    SQLManager SQLManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Context context = this;
        setContentView(R.layout.activity_main);

        checkPermissions();

        SQLManager = SQLManager.getInstance(context);

        final CheckBox requireLogin = findViewById(R.id.cb_RequireLogin);
        final EditText txt_user = findViewById(R.id.txt_username);
        final EditText txt_pass = findViewById(R.id.txt_password);
        final EditText txt_passConfirm = findViewById(R.id.txt_passwordConfirm);
        final CheckBox cb_createAccount = findViewById(R.id.cb_createAccount);
        final Button btn_accountStart = findViewById(R.id.btn_start);
        Button quitButton = findViewById(R.id.btn_quit);

        cb_createAccount.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if(cb_createAccount.isChecked()){
                    txt_passConfirm.setVisibility(txt_passConfirm.VISIBLE);
                    btn_accountStart.setText("CREATE ACCOUNT");
                }else{
                    txt_passConfirm.setVisibility(txt_passConfirm.INVISIBLE);
                    btn_accountStart.setText("LOGIN");
                }
            }
        });

        btn_accountStart.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {


                if(fineLocationPermissionGranted && coarseLocationPermissionGranted) {
                    if(!requireLogin.isChecked()){
                        Intent intent = new Intent(MainActivity.this, MapActivity.class);
                        startActivity(intent);
                    }

                    if(cb_createAccount.isChecked()){
                        createAccount(txt_user.getText().toString(), txt_pass.getText().toString(), txt_passConfirm.getText().toString());
                    }else{
                        if(login(txt_user.getText().toString(), txt_pass.getText().toString())) {
                            Intent intent = new Intent(MainActivity.this, MapActivity.class);
                            startActivity(intent);
                        }
                    }
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

    public void createAccount(String user, String pass, String confirmPass){
        String[] returnMessage = {};
        if(!user.equals("") && !pass.equals("") && !confirmPass.equals("")) {
            if(pass.equals(confirmPass)) {
                returnMessage = SQLManager.createAccount(user, pass);
                if(returnMessage[0].equals("FAILED")){
                    showErrorMessage(returnMessage[1]);
                }else {
                    Toast.makeText(this, "ACCOUNT CREATED", Toast.LENGTH_SHORT).show();
                    resetUI();
                }
            } else { showErrorMessage("PASSWORDS DO NOT MATCH"); }
        } else { showErrorMessage("PLEASE FILL IN ALL TEXT FIELDS"); }
    }

    public boolean login(String user, String pass){
        String[] returnMessage = {};
        if(!user.equals("") && !pass.equals("")){
            returnMessage = SQLManager.checkLogin(user, pass);
            if(returnMessage[0].equals("SUCCESS")){
                return true;
            } else { showErrorMessage(returnMessage[1]); }
        } else { showErrorMessage("PLEASE FILL IN BOTH USERNAME AND PASSWORD"); }
        return false;
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
                }

            }
            case Constants.PERMISSION_REQUEST_ACCESS_COARSE_LOCATION: {
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    coarseLocationPermissionGranted = true;
                    Log.d(TAG, "onRequestPermissionsResult: PERMISSIONS GRANTED");
                } else {
                    coarseLocationPermissionGranted = false;
                    Log.d(TAG, "onRequestPermissionsResult: PERMISSIONS DENIED");
                }
            }
        }
    }

    public void showErrorMessage(String message){
        TextView txt_message = findViewById(R.id.txt_messageToUser);
        txt_message.setText(message);
        txt_message.setVisibility(View.VISIBLE);
    }

    public void resetUI(){
        final EditText txt_user = findViewById(R.id.txt_username);
        final EditText txt_pass = findViewById(R.id.txt_password);
        final EditText txt_passConfirm = findViewById(R.id.txt_passwordConfirm);
        final CheckBox checkBox = findViewById(R.id.cb_createAccount);
        final TextView errorMessage = findViewById(R.id.txt_messageToUser);

        txt_user.setText("");
        txt_pass.setText("");
        txt_passConfirm.setText("");
        checkBox.setChecked(false);
        errorMessage.setVisibility(View.INVISIBLE);
    }
}
