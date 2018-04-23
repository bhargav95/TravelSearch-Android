package com.example.bhargav.travelsearch;

import android.app.ProgressDialog;
import android.graphics.Color;
import android.location.Location;
import android.location.LocationListener;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemSelectedListener;
import android.widget.AutoCompleteTextView;
import android.widget.Spinner;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.places.Places;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.LatLngBounds;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.Polyline;
import com.google.android.gms.maps.model.PolylineOptions;
import com.google.maps.android.PolyUtil;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

public class TabMapFragment extends Fragment implements OnMapReadyCallback, GoogleApiClient.OnConnectionFailedListener, LocationListener {

    private JSONObject js;
    private Double lat, lng;
    private LatLngBounds llb;
    private LatLng ne,sw;
    private SupportMapFragment mapFragment;
    GoogleMap mp;
    private List<Polyline> pyls = new ArrayList<Polyline>();

    private GoogleApiClient aGoogleClient;
    private PlaceAutocompleteAdapter aPlaceAdapter;

    private static LatLngBounds LAT_LNG_BOUNDS;

    private AutoCompleteTextView location;

    public String getTheMapThing(String key1, String key2){

        try {
            return js.getJSONObject("result").getJSONObject("geometry").getJSONObject(key1).get(key2).toString();
        } catch (JSONException e) {
            e.printStackTrace();
            Log.d("myTag",key1);
        }

        return "";
    }

    public String getTheMapThing2(String key1, String key2){

        try {
            return js.getJSONObject("result").getJSONObject("geometry").getJSONObject("viewport").getJSONObject(key1).get(key2).toString();
        } catch (JSONException e) {
            e.printStackTrace();
            Log.d("myTag",key1);
        }

        return "";
    }

    public String getPath(JSONObject googlePathJson) throws JSONException {

        String polyline = googlePathJson.getJSONObject("polyline").getString("points");

        return polyline;
    }

    public String[] getPaths(JSONArray googleStepsJson) throws JSONException {
        int count=googleStepsJson.length();
        String[] polylines = new String[count];

        for(int i=0;i<count;++i){
            polylines[i]=getPath(googleStepsJson.getJSONObject(i));
        }

        return polylines;
    }

    public void setdirections(View vv, int position){
        Log.d("myTag",Integer.toString(position));

        RequestQueue queue = Volley.newRequestQueue(getActivity());
        String url = "https://maps.googleapis.com/maps/api/directions/json?origin=";

        AutoCompleteTextView dirloc = vv.findViewById(R.id.directionAutoCompleteTextView2);
        String origin = dirloc.getText().toString();

        if(origin.matches(""))
            return;

        String destination = "&destination=";

        destination += lat.toString()+","+lng.toString();

        Spinner tmode = vv.findViewById(R.id.directionSpinner);
        String mode = "&mode="+tmode.getSelectedItem().toString().toLowerCase();



        String apikey= "&key=AIzaSyAnLP4Hj91QavGchDnFaw2UHBKToarZ7Ks";
        final ProgressDialog pd = new ProgressDialog(getActivity());
        pd.setMessage("Fetching Directions");
        pd.show();

        // Request a string response from the provided URL.
        StringRequest stringRequest = new StringRequest(Request.Method.GET, url+origin+destination+mode+apikey,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {

                        try {
                            JSONObject js = new JSONObject(response);

                            JSONArray poly = js.getJSONArray("routes").getJSONObject(0).getJSONArray("legs").getJSONObject(0).getJSONArray("steps");

                            String[] paths = getPaths(poly);

                            displaydirections(paths);

                            Log.d("myTag",paths[0]);

                            pd.dismiss();


                        } catch (JSONException e) {
                            Log.d("myTag", "h");
                        }


                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Log.d("myTag","That didn't work!");
            }
        });

        // Add the request to the RequestQueue.
        queue.add(stringRequest);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        final View vv =  inflater.inflate(R.layout.tabmap_fragment, container, false);


        js = ((PlaceDetailsActivity)this.getActivity()).res;

        try {
            Log.d("myTag",js.getJSONObject("result").getJSONObject("geometry").toString());
            lat = Double.parseDouble(getTheMapThing("location","lat"));
            lng = Double.parseDouble(getTheMapThing("location","lng"));

            ne  = new LatLng(
                    Double.parseDouble(getTheMapThing2("northeast","lat")),
                    Double.parseDouble(getTheMapThing2("northeast","lng")));
            sw  = new LatLng(
                    Double.parseDouble(getTheMapThing2("southwest","lat")),
                    Double.parseDouble(getTheMapThing2("southwest","lng")));

            llb = new LatLngBounds(sw,ne);
            LAT_LNG_BOUNDS = llb;

        } catch (JSONException e) {
            e.printStackTrace();
        }

        Log.d("myTag",lat.toString());
        Log.d("myTag",lng.toString());


        if(aGoogleClient==null|| !aGoogleClient.isConnected()){
            try{
                aGoogleClient = new GoogleApiClient
                        .Builder(getActivity())
                        .addApi(Places.GEO_DATA_API)
                        .addApi(Places.PLACE_DETECTION_API)
                        .enableAutoManage(getActivity(), this)
                        .build();
            }
            catch (Exception e){

            }

        }


        aPlaceAdapter = new PlaceAutocompleteAdapter(getActivity(), aGoogleClient, LAT_LNG_BOUNDS, null);

        location =vv.findViewById(R.id.directionAutoCompleteTextView2);
        location.setAdapter(aPlaceAdapter);


        mapFragment = (SupportMapFragment) this.getChildFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);

        location.setOnItemClickListener(new AdapterView.OnItemClickListener() {

            @Override
            public void onItemClick(AdapterView<?> parent, View arg1, int pos,
                                    long id) {

                setdirections(vv, pos);

            }
        });

        Spinner spinner = vv.findViewById(R.id.directionSpinner);
        spinner.setOnItemSelectedListener(new OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parentView, View selectedItemView, int position, long id) {
                // your code here

                setdirections(vv, position);

            }

            @Override
            public void onNothingSelected(AdapterView<?> parentView) {
                // your code here

                setdirections(vv,0);
            }

        });

        return vv;
    }



    @Override
    public void onMapReady(GoogleMap googleMap) {

        mp = googleMap;
        // Add a marker in Sydney, Australia,
        // and move the map's camera to the same location.
        LatLng sydney = new LatLng(lat, lng);
        googleMap.addMarker(new MarkerOptions().position(sydney)
                .title("Marker in Sydney"));

        googleMap.moveCamera(CameraUpdateFactory.newLatLngZoom(sydney,17));
        //googleMap.setLatLngBoundsForCameraTarget(llb);
        //googleMap.moveCamera(CameraUpdateFactory.newLatLngBounds(llb,0));
    }

    @Override
    public void onLocationChanged(Location location) {

    }

    @Override
    public void onStatusChanged(String provider, int status, Bundle extras) {

    }

    @Override
    public void onProviderEnabled(String provider) {

    }

    @Override
    public void onProviderDisabled(String provider) {

    }

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {

    }

    public void displaydirections(String[] directionsList){

        int count=directionsList.length;

        for(Polyline line : pyls)
        {
            line.remove();
        }

        pyls.clear();

        for(int i=0;i<count;++i){

            PolylineOptions opt = new PolylineOptions();

            opt.color(Color.BLUE);
            opt.width(10);
            opt.addAll(PolyUtil.decode(directionsList[i]));


            Polyline p = mp.addPolyline(opt);

            pyls.add(p);
        }

    }
}
