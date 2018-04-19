package com.example.bhargav.travelsearch;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.AutoCompleteTextView;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;

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

public class MainActivity extends AppCompatActivity implements GoogleApiClient.OnConnectionFailedListener{

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

        aGoogleClient = new GoogleApiClient
                .Builder(this)
                .addApi(Places.GEO_DATA_API)
                .addApi(Places.PLACE_DETECTION_API)
                .enableAutoManage(this, this)
                .build();

        aPlaceAdapter = new PlaceAutocompleteAdapter(this, aGoogleClient, LAT_LNG_BOUNDS, null);

        location.setAdapter(aPlaceAdapter);
    }

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {

    }

    public void clear(View view){



    }

    public void search(View view){
        Log.d("myTag", "This is my message");

        final TextView mTextView = (TextView) findViewById(R.id.resultTextView);
        final EditText k = (EditText) findViewById(R.id.keywordEditText);
        final Spinner cat = (Spinner) findViewById(R.id.CategorySpinner);
        final EditText distance = (EditText) findViewById(R.id.distanceEditText);

        RequestQueue queue = Volley.newRequestQueue(this);

        String url = "http://node-express-env.am4vuh8cpm.us-west-1.elasticbeanstalk.com/nearby?keyword=";
        String key = k.getText().toString();
        String other="&location=34.0093,-118.2584&place=&radio=option1&radius=16000&type=";
        String category_text = cat.getSelectedItem().toString().toLowerCase().replaceAll(" ","_");

        final ProgressDialog pd = new ProgressDialog(MainActivity.this);
        pd.setMessage("Fetching Results");
        pd.show();

        // Request a string response from the provided URL.
        StringRequest stringRequest = new StringRequest(Request.Method.GET, url+key+other+category_text,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {

                        try{
                            JSONObject js= new JSONObject(response);
                            JSONArray res = js.getJSONArray("results");
                            // Display the first 500 characters of the response string.

                            String namesOfPlaces = "";

                            for (int i = 0; i < res.length(); i++) {
                                JSONObject row = res.getJSONObject(i);
                                namesOfPlaces += row.get("name")+"\n";
                            }

                            Intent intent = new Intent(MainActivity.this,ResultsActivity.class);
                            intent.putExtra(EXTRA_MESSAGE, namesOfPlaces);
                            startActivity(intent);

                            pd.dismiss();


                        }
                        catch(JSONException e){
                            Log.d("myTag","h");
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
}
