package com.example.canteenchecker.canteenmanager.ui;

import android.content.Context;
import android.content.Intent;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.os.AsyncTask;
import android.support.v4.app.Fragment;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;

import com.example.canteenchecker.canteenmanager.CanteenManagerApplication;
import com.example.canteenchecker.canteenmanager.R;
import com.example.canteenchecker.canteenmanager.core.Canteen;
import com.example.canteenchecker.canteenmanager.proxy.ServiceProxy;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.UiSettings;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;

import java.io.IOException;
import java.util.List;
import java.util.Locale;

public class EditCanteenDataActivity extends AppCompatActivity {
    private EditText edtCanteenName;
    private EditText edtCanteenWebsite;
    private SeekBar skbCanteenWaitTime;
    private EditText edtCanteenAddress;
    private SupportMapFragment mpfMap;
    private TextView seekbarLabel;
    private Button btnSaveChanges;

    private Context context;


    private Canteen currentCanteenState;

    private static final String TAG = EditCanteenDataActivity.class.toString();
    private static final int DEFAULTMAP_ZOOM_FACTOR = 17;

    public static Intent createIntent(Context context) {
        Intent intent = new Intent(context, EditCanteenDataActivity.class);
        return intent;

    }
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_canteen_data);

        context = this;

        edtCanteenName = findViewById(R.id.edtCanteenName);
        edtCanteenWebsite = findViewById(R.id.edtCanteenWebsite);
        skbCanteenWaitTime = findViewById(R.id.skbCanteenWaitTime);
        edtCanteenAddress = findViewById(R.id.edtCanteenAddress);
        seekbarLabel = findViewById(R.id.seekbarLabel);
        btnSaveChanges = findViewById(R.id.btnSaveChanges);

        //Google Map
        mpfMap = (SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.mpfMap);
        mpfMap.getMapAsync(new OnMapReadyCallback() {
            @Override
            public void onMapReady(GoogleMap googleMap) {
                UiSettings uiSettings = googleMap.getUiSettings();
                //uiSettings.setAllGesturesEnabled(false);
                uiSettings.setZoomControlsEnabled(true);
                googleMap.setOnMapLongClickListener(new GoogleMap.OnMapLongClickListener() {
                    @Override
                    public void onMapLongClick(LatLng latLng) {
                        Geocoder geocoder = new Geocoder(context, Locale.getDefault());
                        try {
                            List<Address> addresses = geocoder.getFromLocation(latLng.latitude, latLng.longitude, 1);
                            edtCanteenAddress.setText(addresses.get(0).getAddressLine(0));
                        } catch (IOException e) {
                            Toast.makeText(context, "Unable to find address, try again.", Toast.LENGTH_SHORT);
                        }
                    }
                });
            }
        });

        //Progress Bar
        skbCanteenWaitTime.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean b) {
                int val = (progress * (seekBar.getWidth() - 2 * seekBar.getThumbOffset())) / seekBar.getMax();
                seekbarLabel.setText("" + progress);
                seekbarLabel.setX(seekBar.getX() + val + seekBar.getThumbOffset() / 2);
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {

            }
        });

        //SaveButton
        btnSaveChanges.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                currentCanteenState.setName(edtCanteenName.getText().toString());
                currentCanteenState.setLocation(edtCanteenAddress.getText().toString());
                currentCanteenState.setAverageWaitingTime(skbCanteenWaitTime.getProgress());
                currentCanteenState.setWebsite(edtCanteenWebsite.getText().toString());
                saveCanteenData(currentCanteenState);
                finish();
            }
        });
        updateCanteenData();
    }

    private void updateCanteenData() {
        new AsyncTask<Void, Void, Canteen>(){

            protected Canteen doInBackground(Void... voids) {
                try {
                    return new ServiceProxy().getCanteen("Bearer " + CanteenManagerApplication.getInstance().getAuthenticationToken());
                } catch (IOException e) {
                    Log.e(TAG, "Error receiving Canteen data");
                    return null;
                }
            }

            @Override
            protected void onPostExecute(Canteen canteen) {
                if(canteen != null){
                    currentCanteenState = canteen;
                    edtCanteenName.setText(canteen.getName());
                    edtCanteenWebsite.setText(canteen.getWebsite());
                    skbCanteenWaitTime.setProgress(canteen.getAverageWaitingTime());
                    edtCanteenAddress.setText(canteen.getLocation());

                    new AsyncTask<String, Void, LatLng>() {
                        @Override
                        protected LatLng doInBackground(String... strings) {
                            LatLng location = null;
                            Geocoder geocoder = new Geocoder(EditCanteenDataActivity.this);
                            try {
                                List<Address> addresses = geocoder.getFromLocationName(strings[0], 1);
                                if(addresses != null && addresses.size() > 0) {
                                    Address address = addresses.get(0);
                                    location = new LatLng(address.getLatitude(), address.getLongitude());
                                } else {
                                    Log.w(TAG, "Resolving failed!");
                                }
                            } catch (IOException e) {
                                Log.w(TAG, "Resolving of Adress failed.");
                            }
                            return location;
                        }

                        @Override
                        protected void onPostExecute(final LatLng latLng) {
                            mpfMap.getMapAsync(new OnMapReadyCallback() {
                                @Override
                                public void onMapReady(GoogleMap googleMap) {
                                    googleMap.clear();
                                    if(latLng != null) {
                                        googleMap.addMarker(new MarkerOptions().position(latLng));
                                        googleMap.animateCamera(CameraUpdateFactory.newLatLngZoom(latLng, DEFAULTMAP_ZOOM_FACTOR));
                                    } else {
                                        googleMap.animateCamera(CameraUpdateFactory.newLatLngZoom(new LatLng(0, 0), 0));
                                    }
                                }
                            });
                        }
                    }.execute(canteen.getLocation());
                }
            }
        }.execute();
    }

    private void saveCanteenData(Canteen currentCanteenState) {
        new AsyncTask<Canteen, Void, Boolean>(){
            @Override
            protected Boolean doInBackground(Canteen... canteens) {
                if (canteens != null && canteens[0] != null) {
                    try {
                        new ServiceProxy().updateCanteen(
                                "Bearer " + CanteenManagerApplication.getInstance().getAuthenticationToken(),
                                canteens[0]);

                    } catch (IOException e) {
                        return false;
                    }
                    return true;
                }
                return false;
            }
            @Override
            protected void onPostExecute(Boolean aBoolean) {
                super.onPostExecute(aBoolean);
                if(aBoolean){
                    Toast.makeText(getApplicationContext(), getString(R.string.canteenSuccessfullySavedMsg), Toast.LENGTH_SHORT).show();
                } else {
                    Toast.makeText(getApplicationContext(), getString(R.string.saveUnsuccessful), Toast.LENGTH_SHORT);
                }
            }


        }.execute(currentCanteenState);
    }
}
