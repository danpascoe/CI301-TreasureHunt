package application.pascoe.com.ci301.utility;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.drawable.AnimationDrawable;
import android.location.Location;
import android.location.LocationManager;
import android.os.Bundle;
import android.support.v4.app.ActivityCompat;
import android.support.v7.widget.CardView;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

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
import android.support.v4.app.FragmentActivity;
import application.pascoe.com.ci301.R;
import application.pascoe.com.ci301.constants.Constants;
import application.pascoe.com.ci301.gameplay.ClueLocated;
import application.pascoe.com.ci301.gameplay.Gameplay;
import application.pascoe.com.ci301.sqlite.AndroidDatabaseManager;

public class MapActivity extends FragmentActivity implements OnMapReadyCallback{

    private GoogleMap mMap;
    private FusedLocationProviderClient fusedLocationClient;
    private LocationManager locationManager;
    public Gameplay gameplay;

    public boolean huntActive = false;

    String[] currentClueSet = {};
    private int currentClue = 0;
    private int clueSetLength;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_map);

        fusedLocationClient = LocationServices.getFusedLocationProviderClient(this);
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);

        huntActive();

        final EditText txt_huntID = findViewById(R.id.txt_huntID);
        Button btn_huntStart = findViewById(R.id.btn_huntStart);
        btn_huntStart.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                huntActive = true;
                huntActive();
                GameStart(txt_huntID.getText().toString());
            }
        });

        Button btn_NextClue = findViewById(R.id.btn_nextClue);
        btn_NextClue.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                nextClue();
            }
        });

        Button btn_checkDist = findViewById(R.id.btn_checkDistance);
        btn_checkDist.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                checkPlayerRange();

            }
        });

        Button btn_debugDB = findViewById(R.id.btn_showDB);
        btn_debugDB.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent dbmanager = new Intent(MapActivity.this, AndroidDatabaseManager.class);
                startActivity(dbmanager);
                //https://github.com/sanathp/DatabaseManager_For_Android
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

    void GameStart(String huntID){
        gameplay = new Gameplay();
        gameplay.GameInitiate(this, huntID);
        updateClueSet();
    }

    private void checkPlayerRange() {
        if (ActivityCompat.checkSelfPermission(MapActivity.this, Manifest.permission.ACCESS_FINE_LOCATION)
                != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(MapActivity.this, Manifest.permission.ACCESS_COARSE_LOCATION)
                != PackageManager.PERMISSION_GRANTED) {
            return;
        }

        fusedLocationClient.getLastLocation().addOnSuccessListener(this, new OnSuccessListener<Location>() {
            @Override
            public void onSuccess(Location location){
                LatLng currentPosition = new LatLng(location.getLatitude(), location.getLongitude());
                boolean distanceCheck = gameplay.checkDistance(currentPosition);

                if(distanceCheck){
                    playCheckAnimation(ClueLocated.InRange);
                    updateClueSet();
                }else{
                    playCheckAnimation(ClueLocated.OutOfRange);
                }
            }
        });
    }

    private void updateClueSet() {
        currentClueSet =  gameplay.getClues();
        clueSetLength = currentClueSet.length;
        currentClue = 0;
        showCurrentClue(currentClue);
    }

    private void showCurrentClue(int currentClue){
        TextView txt_clue = findViewById(R.id.txt_clue);
        TextView txt_clueNum = findViewById(R.id.txt_clueNumber);
        txt_clueNum.setText("Clue Number " + (currentClue + 1) + " of " + currentClueSet.length);
        txt_clue.setText(currentClueSet[currentClue]);
    }

    private void nextClue() {
        if (currentClue < clueSetLength - 1 && clueSetLength != 1) {
            currentClue++;
            showCurrentClue(currentClue);
        } else {
            currentClue = 0;
            showCurrentClue(currentClue);
        }
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

    public void endGame(){
        Button checkButton = findViewById(R.id.btn_checkDistance);
        TextView txt_clue = findViewById(R.id.txt_clue);
        TextView txt_clueNum = findViewById(R.id.txt_clueNumber);
        txt_clue.setText("You did it!");
        txt_clueNum.setText("Congratulations");
        checkButton.setEnabled(false);
    }

    public void huntActive(){
        CardView cv_clue = findViewById(R.id.cv_clue);
        CardView cv_start = findViewById(R.id.cv_start);
        Button btn_NextClue = findViewById(R.id.btn_nextClue);
        Button btn_checkDist = findViewById(R.id.btn_checkDistance);
        Button btn_showHideClue = findViewById(R.id.btn_showHideClue);

        if(!huntActive){
            cv_clue.setVisibility(View.INVISIBLE);
            cv_start.setVisibility(View.VISIBLE);
            btn_NextClue.setVisibility(View.INVISIBLE);
            btn_checkDist.setVisibility(View.INVISIBLE);
            btn_showHideClue.setVisibility(View.INVISIBLE);
        }else{
            cv_clue.setVisibility(View.VISIBLE);
            cv_start.setVisibility(View.INVISIBLE);
            btn_NextClue.setVisibility(View.VISIBLE);
            btn_checkDist.setVisibility(View.VISIBLE);
            btn_showHideClue.setVisibility(View.VISIBLE);
        }
    }
}