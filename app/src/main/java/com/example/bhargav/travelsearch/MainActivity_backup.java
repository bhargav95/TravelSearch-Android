package com.example.bhargav.travelsearch;

import android.Manifest;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Criteria;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.places.Places;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.LatLngBounds;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

public class MainActivity_backup extends AppCompatActivity implements GoogleApiClient.OnConnectionFailedListener, LocationListener {

    private TextView latituteField;
    private TextView longitudeField;
    private LocationManager locationManager;
    private String provider;

    private double latlat;
    private double lonlon;

    public static final String EXTRA_MESSAGE = "com.example.bhargav.travelsearch.MESSAGE";

    private GoogleApiClient aGoogleClient;
    private PlaceAutocompleteAdapter aPlaceAdapter;

    private static final LatLngBounds LAT_LNG_BOUNDS = new LatLngBounds(
            new LatLng(-40, -168), new LatLng(71, 136));

    private AutoCompleteTextView location;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        location = findViewById(R.id.autoCompleteTextView);

        latituteField = (TextView) findViewById(R.id.TextView02);
        longitudeField = (TextView) findViewById(R.id.TextView04);

        String[] permissions = {Manifest.permission.ACCESS_FINE_LOCATION,
                Manifest.permission.ACCESS_COARSE_LOCATION};

        Integer LOC_REQ_CODE = 200;

        // Get the location manager
        locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
        // Define the criteria how to select the locatioin provider -> use
        // default
        Criteria criteria = new Criteria();
        provider = locationManager.getBestProvider(criteria, false);

        Log.d("myTag",provider);

        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {

            Log.d("myTag","permission not set");

            ActivityCompat.requestPermissions(this,
                    permissions,
                    LOC_REQ_CODE);

            // TODO: Consider calling
            //    ActivityCompat#requestPermissions
            // here to request the missing permissions, and then overriding
            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
            //                                          int[] grantResults)
            // to handle the case where the user grants the permission. See the documentation
            // for ActivityCompat#requestPermissions for more details.
            return;
        }
        Location locationl = locationManager.getLastKnownLocation(provider);

        // Initialize the location fields
        if (locationl != null) {

            Log.d("myTag","if");

            System.out.println("Provider " + provider + " has been selected.");
            onLocationChanged(locationl);
        } else {

            Log.d("myTag","else");

            latituteField.setText("Location not available");
            longitudeField.setText("Location not available");
        }

        aGoogleClient = new GoogleApiClient
                .Builder(this)
                .addApi(Places.GEO_DATA_API)
                .addApi(Places.PLACE_DETECTION_API)
                .enableAutoManage(this, this)
                .build();

        aPlaceAdapter = new PlaceAutocompleteAdapter(this, aGoogleClient, LAT_LNG_BOUNDS, null);

        location.setAdapter(aPlaceAdapter);

        RadioGroup rgroup = (RadioGroup) findViewById(R.id.LocationRadioGroup);

        rgroup.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            public void onCheckedChanged(RadioGroup group, int checkedId) {
                // checkedId is the RadioButton selected

                AutoCompleteTextView actv = findViewById(R.id.autoCompleteTextView);

                switch (checkedId) {
                    case R.id.radioButton2:

                        Log.d("myTag", "radcheck1: ");
                        actv.setText("");
                        actv.setEnabled(false);
                        break;
                    case R.id.radioButton:
                        // Fragment 2
                        Log.d("myTag", "radcheck2: ");

                        actv.setEnabled(true);
                        break;
                }
            }
        });
    }

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {

    }

    public void clear(View view) {

        TextView error1 = findViewById(R.id.alertKeyTextView2);
        TextView error2 = findViewById(R.id.alertLocTextView);

        error1.setVisibility(View.GONE);
        error2.setVisibility(View.GONE);

        EditText key = findViewById(R.id.keywordEditText);
        EditText dist = findViewById(R.id.distanceEditText);
        AutoCompleteTextView loc = findViewById(R.id.autoCompleteTextView);

        key.setText("");
        dist.setText("");
        loc.setText("");

        String compareValue = "Default";
        Spinner mSpinner = findViewById(R.id.CategorySpinner);
        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(this, R.array.categoryStringArray, android.R.layout.simple_spinner_item);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        mSpinner.setAdapter(adapter);
        if (compareValue != null) {
            int spinnerPosition = adapter.getPosition(compareValue);
            mSpinner.setSelection(spinnerPosition);
        }

        RadioButton rad = findViewById(R.id.radioButton2);
        AutoCompleteTextView actv = findViewById(R.id.autoCompleteTextView);

        rad.setChecked(true);

    }

    public boolean validate(){

        TextView error1 = (TextView) findViewById(R.id.alertKeyTextView2);
        TextView error2 = (TextView) findViewById(R.id.alertLocTextView);

        boolean flag=false;

        EditText keyword = (EditText) findViewById(R.id.keywordEditText);

        if(keyword.getText().toString().matches("")){

            error1.setVisibility(View.VISIBLE);

            flag|=true;
        }
        else{
            error1.setVisibility(View.GONE);
        }

        RadioButton r = (RadioButton) findViewById(R.id.radioButton);
        AutoCompleteTextView actv = (AutoCompleteTextView) findViewById(R.id.autoCompleteTextView);

        if(r.isChecked() && actv.getText().toString().matches("")){

            error2.setVisibility(View.VISIBLE);
            flag|=true;
        }
        else{
            error2.setVisibility(View.GONE);
        }

        return !flag;

    }

    public void search(View view) {

        if(!validate()){
            return;
        }

        Log.d("myTag", "This is my message");

        final TextView mTextView = (TextView) findViewById(R.id.resultTextView);
        final EditText k = (EditText) findViewById(R.id.keywordEditText);
        final Spinner cat = (Spinner) findViewById(R.id.CategorySpinner);
        final EditText distance = (EditText) findViewById(R.id.distanceEditText);

        RequestQueue queue = Volley.newRequestQueue(this);

        String url = "http://node-express-env.am4vuh8cpm.us-west-1.elasticbeanstalk.com/nearby?keyword=";
        String key = k.getText().toString();


        String loc = "&location="+String.valueOf(latlat)+","+String.valueOf(lonlon);


        String other = "&place="+location.getText().toString();

        RadioButton r= findViewById(R.id.radioButton);

        String radioOption;
        if(r.isChecked()){
            radioOption = "&radio=option2";
        }
        else{
            radioOption = "&radio=option1";
        }

        EditText rad =(EditText) findViewById(R.id.distanceEditText);

        String radius;
        if(rad.getText().toString().matches("")){
            radius= "&radius=16000";
        }
        else{
            radius= "&radius="+String.valueOf(Integer.parseInt(rad.getText().toString())*1600);
        }

        String category_text = "&type="+cat.getSelectedItem().toString().toLowerCase().replaceAll(" ", "_");

        Log.d("myTag",url+key+loc+other+radioOption+radius+category_text);

        final ProgressDialog pd = new ProgressDialog(MainActivity_backup.this);
        pd.setMessage("Fetching Results");
        pd.show();

        // Request a string response from the provided URL.
        StringRequest stringRequest = new StringRequest(Request.Method.GET, url+key+loc+other+radioOption+radius+category_text,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {

                        try {
                            JSONObject js = new JSONObject(response);
                            JSONArray res = js.getJSONArray("results");
                            // Display the first 500 characters of the response string.

                            String namesOfPlaces = "";

                            for (int i = 0; i < res.length(); i++) {
                                JSONObject row = res.getJSONObject(i);
                                namesOfPlaces += row.get("name") + "\n";
                            }

                            Intent intent = new Intent(MainActivity_backup.this, ResultsActivity.class);
                            intent.putExtra(EXTRA_MESSAGE, res.toString());
                            startActivity(intent);

                            pd.dismiss();


                        } catch (JSONException e) {
                            Log.d("myTag", "h");
                        }


                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                mTextView.setText("That didn't work!");
            }
        });

// Add the request to the RequestQueue.
        queue.add(stringRequest);
    }

    /* Request updates at startup */
    @Override
    protected void onResume() {
        super.onResume();
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            // TODO: Consider calling
            //    ActivityCompat#requestPermissions
            // here to request the missing permissions, and then overriding
            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
            //                                          int[] grantResults)
            // to handle the case where the user grants the permission. See the documentation
            // for ActivityCompat#requestPermissions for more details.
            return;
        }
        locationManager.requestLocationUpdates(provider, 400, 1, this);
    }

    /* Remove the locationlistener updates when Activity is paused */
    @Override
    protected void onPause() {
        super.onPause();
        locationManager.removeUpdates(this);
    }

    @Override
    public void onLocationChanged(Location location) {

        latlat = location.getLatitude();
        lonlon = location.getLongitude();

        int lat = (int) latlat;
        int lng = (int) lonlon;

        Log.d("myTag", String.valueOf(lat));
        Log.d("myTag", String.valueOf(lng));

        latituteField.setText(String.valueOf(lat));
        longitudeField.setText(String.valueOf(lng));
    }

    @Override
    public void onStatusChanged(String provider, int status, Bundle extras) {

    }

    @Override
    public void onProviderEnabled(String provider) {
        Toast.makeText(this, "Enabled new provider " + provider,
                Toast.LENGTH_SHORT).show();

    }

    @Override
    public void onProviderDisabled(String provider) {
        Toast.makeText(this, "Disabled provider " + provider,
                Toast.LENGTH_SHORT).show();
    }
}
