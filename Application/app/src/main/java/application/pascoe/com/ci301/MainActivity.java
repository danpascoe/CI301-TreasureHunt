package application.pascoe.com.ci301;

        import android.Manifest;
        import android.content.Intent;
        import android.content.pm.PackageManager;
        import android.support.annotation.NonNull;
        import android.support.v4.app.ActivityCompat;
        import android.support.v4.content.ContextCompat;
        import android.support.v7.app.AppCompatActivity;
        import android.os.Bundle;
        import android.view.View;
        import android.widget.Button;
        import android.widget.Toast;

public class MainActivity extends AppCompatActivity {

    private static final String TAG = "MainActivity";
    private boolean locationPermissionGranted = false;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Button button = findViewById(R.id.btn_start);
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(locationPermissionGranted) {
                    Intent intent = new Intent(MainActivity.this, MapActivity.class);
                    startActivity(intent);
                }
            }
        });
        checkPermissions();
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
            locationPermissionGranted = true;
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        switch (requestCode) {
            case Constants.PERMISSION_REQUEST_ACCESS_FINE_LOCATION: {
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    locationPermissionGranted = true;

                } else {
                    locationPermissionGranted = false;
                    // SHOW DIALOG & REPEAT REQUEST PERMISSIONS
                }
            }
        }
    }
}
