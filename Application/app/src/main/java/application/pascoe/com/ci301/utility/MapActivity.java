package application.pascoe.com.ci301.utility;

import android.Manifest;
import android.content.Context;
import android.content.pm.PackageManager;
import android.graphics.drawable.AnimationDrawable;
import android.location.Location;
import android.location.LocationManager;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.FragmentActivity;
import android.os.Bundle;
import android.support.v7.widget.CardView;
import android.view.View;
import android.view.animation.Animation;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MapStyleOptions;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.tasks.OnSuccessListener;

import application.pascoe.com.ci301.R;
import application.pascoe.com.ci301.constants.Constants;
import application.pascoe.com.ci301.gameplay.ClueLocated;
import application.pascoe.com.ci301.gameplay.Gameplay;

public class MapActivity extends FragmentActivity implements OnMapReadyCallback{

    private static final String TAG = "MapActivity";
    private GoogleMap mMap;
    private FusedLocationProviderClient fusedLocationClient;
    private LocationManager locationManager;
    private Animation.AnimationListener listener;
    private AnimationDrawable tickAnimation;
    private AnimationDrawable crossAnimation;
    private int TotalPostions;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_map);
        fusedLocationClient = LocationServices.getFusedLocationProviderClient(this);
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);
        TotalPostions = Gameplay.GameInitiate();
        updateClue();
        Button checkButton = findViewById(R.id.btn_checkDistance);
        checkButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                checkPlayerRange();
            }
        });

        final Button btn_showHideClue = findViewById(R.id.btn_showHideClue);
        btn_showHideClue.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                CardView cv_clue = findViewById(R.id.cv_clue);
                if(cv_clue.getVisibility() == cv_clue.VISIBLE) {
                    cv_clue.setVisibility(cv_clue.INVISIBLE);
                    btn_showHideClue.setText("SHOW CLUE");
                }else{
                    cv_clue.setVisibility(cv_clue.VISIBLE);
                    btn_showHideClue.setText("HIDE CLUE");
                }
            }
        });
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;
        mMap.setBuildingsEnabled(true);
        mMap.setMinZoomPreference(Constants.MIN_CAMERA_ZOOM);
        mMap.setMaxZoomPreference(Constants.MAX_CAMERA_ZOOM);
        mMap.getUiSettings().setScrollGesturesEnabled(false);
        mMap.setMapStyle(MapStyleOptions.loadRawResourceStyle(this, R.raw.mapstyle));

        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED
                && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            return;
        }
        mMap.setMyLocationEnabled(false);

       fusedLocationClient.getLastLocation().addOnSuccessListener(this, new OnSuccessListener<Location>() {
            @Override
            public void onSuccess(Location location) {
            locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);

            if (ActivityCompat.checkSelfPermission(MapActivity.this, Manifest.permission.ACCESS_FINE_LOCATION)
                    != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(MapActivity.this, Manifest.permission.ACCESS_COARSE_LOCATION)
                    != PackageManager.PERMISSION_GRANTED) {
                return;
            }

            if (location != null) {
                double mLat = location.getLatitude();
                double mLong = location.getLongitude();

                LatLng pos = new LatLng(mLat, mLong);
                updateLocation(pos);
            }

            locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, Constants.MIN_UPDATE_TIME, Constants.MIN_UPDATE_DISTANCE, new android.location.LocationListener() {
                @Override
                public void onLocationChanged(Location location) {
                    if (location != null) {
                        double mLat = location.getLatitude();
                        double mLong = location.getLongitude();

                        LatLng pos = new LatLng(mLat, mLong);

                        updateLocation(pos);
                    }
                }

                @Override
                public void onStatusChanged(String s, int i, Bundle bundle) {}

                @Override
                public void onProviderEnabled(String s) {}

                @Override
                public void onProviderDisabled(String s) {}
            });
            }
        });
    }

    private void updateLocation(LatLng pos) {
        mMap.clear();
        mMap.addMarker(new MarkerOptions()
                .position(pos)
                .icon(BitmapDescriptorFactory.fromResource(R.drawable.feet_50x50))
                .title("User Position"));
        mMap.moveCamera(CameraUpdateFactory.newLatLng(pos));
    }

    private void checkPlayerRange(){
        if (ActivityCompat.checkSelfPermission(MapActivity.this, Manifest.permission.ACCESS_FINE_LOCATION)
                != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(MapActivity.this, Manifest.permission.ACCESS_COARSE_LOCATION)
                != PackageManager.PERMISSION_GRANTED) {
            return;
        }

        fusedLocationClient.getLastLocation().addOnSuccessListener(this, new OnSuccessListener<Location>() {
            @Override
            public void onSuccess(Location location) {
            locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
                if (location != null) {
                    double mLat = location.getLatitude();
                    double mLong = location.getLongitude();

                    LatLng pos = new LatLng(mLat, mLong);
                    updateLocation(pos);

                    boolean distanceCheck = Gameplay.checkDistance(pos);

                    if(distanceCheck){
                        playCheckAnimation(ClueLocated.InRange);
                        Toast.makeText(MapActivity.this, "CLUE FOUND", Toast.LENGTH_SHORT).show();
                        updateClue();
                    }else{
                        playCheckAnimation(ClueLocated.OutOfRange);
                        Toast.makeText(MapActivity.this, "CLUE NOT FOUND", Toast.LENGTH_SHORT).show();
                    }
                }
            }
        });
    }

    public void playCheckAnimation(ClueLocated rangeCheck) {
        if(rangeCheck == ClueLocated.InRange) {
            ImageView anim_tick = findViewById(R.id.anim_tick);
            anim_tick.setBackgroundResource(R.drawable.tick_animation);
            AnimationDrawable tickAnimation = (AnimationDrawable) anim_tick.getBackground();
            tickAnimation.stop();
            tickAnimation.start();
        }else{
            ImageView anim_cross = findViewById(R.id.anim_cross);
            anim_cross.setBackgroundResource(R.drawable.cross_animation);
            AnimationDrawable crossAnimation = (AnimationDrawable) anim_cross.getBackground();
            crossAnimation.stop();
            crossAnimation.start();
        }
    }

    public void updateClue(){
        String[] currentClueArr = Gameplay.getClue();
        TextView txt_clue = findViewById(R.id.txt_clue);
        TextView txt_clueNum = findViewById(R.id.txt_clueNumber);

        if(Integer.parseInt(currentClueArr[0]) >= (TotalPostions + 1)){
            endGame();
            return;
        }

        txt_clueNum.setText("Clue Number " + (currentClueArr[0]));
        txt_clue.setText(currentClueArr[1]);
    }

    public void endGame(){
        Button checkButton = findViewById(R.id.btn_checkDistance);
        TextView txt_clue = findViewById(R.id.txt_clue);
        TextView txt_clueNum = findViewById(R.id.txt_clueNumber);
        txt_clue.setText("You did it!");
        txt_clueNum.setText("Congratulations");
        checkButton.setEnabled(false);
    }
}