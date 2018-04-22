package com.example.bhargav.travelsearch;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

public class ResultsActivity extends AppCompatActivity implements MyRecyclerViewAdapter.ItemClickListener {

    MyRecyclerViewAdapter adapter;
    public static final String PLACE_MESSAGE = "com.example.bhargav.travelsearch.PLACE_MESSAGE";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_results);


        // Get the Intent that started this activity and extract the string
        Intent intent = getIntent();
        try {
            JSONArray message = new JSONArray(intent.getStringExtra(TabSearchFragment.EXTRA_MESSAGE));

            // Capture the layout's TextView and set the string as its text
            //TextView textView = findViewById(R.id.resultTextView);
            //textView.setText(message);

            // data to populate the RecyclerView with
            ArrayList<String> animalNames = new ArrayList<>();
            animalNames.add("Horse");


            // set up the RecyclerView
            RecyclerView recyclerView = findViewById(R.id.rvPlaces);
            recyclerView.setLayoutManager(new LinearLayoutManager(this));
            //adapter = new MyRecyclerViewAdapter(this, animalNames);
            adapter = new MyRecyclerViewAdapter(this, message);
            adapter.setClickListener(this);
            recyclerView.setAdapter(adapter);

        } catch (JSONException e) {

        }

    }

    @Override
    public void onItemClick(View view, final int position) {

        final ProgressDialog pd = new ProgressDialog(this);
        pd.setMessage("Fetching Place Info");
        pd.show();

        String place_id = "";
        try {
            place_id = adapter.getItem(position).get("place_id").toString();
        } catch (JSONException e) {
            e.printStackTrace();
        }

        RequestQueue queue = Volley.newRequestQueue(this);

        String url = "http://my-cloned-env-vasuki.us-west-2.elasticbeanstalk.com/placedetails?placeid=" + place_id;

        Log.d("myTag", url);

        // Request a string response from the provided URL.
        StringRequest stringRequest = new StringRequest(Request.Method.GET, url,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {

                        Log.d("myTag", "done");

                        try {


                            JSONObject js = new JSONObject(response);

                            Intent intent = new Intent(ResultsActivity.this, PlaceDetailsActivity.class);
                            intent.putExtra(PLACE_MESSAGE, js.toString());
                            startActivity(intent);

                            pd.dismiss();


                        } catch (JSONException e) {
                            Log.d("myTag", "h");
                        }


                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {

            }
        });

        queue.add(stringRequest);

        //Toast.makeText(this, "You clicked " + adapter.getItem(position) + " on row number " + position, Toast.LENGTH_SHORT).show();
    }
}
