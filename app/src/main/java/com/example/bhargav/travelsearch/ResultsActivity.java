package com.example.bhargav.travelsearch;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

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
    JSONArray message;
    JSONArray message1;
    JSONArray message2;

    String token1="";
    String token2="";

    int currentIndex;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_results);


        // Get the Intent that started this activity and extract the string
        Intent intent = getIntent();
        try {
            message = new JSONArray(intent.getStringExtra(TabSearchFragment.EXTRA_MESSAGE));
            message1=null;
            message2=null;

            token1 = intent.getStringExtra(TabSearchFragment.TOKEN_MESSAGE);

            if(token1.matches("")){

                findViewById(R.id.nextButton).setEnabled(false);
            }


            // Capture the layout's TextView and set the string as its text
            //TextView textView = findViewById(R.id.resultTextView);
            //textView.setText(message);

            currentIndex = 0;

            // data to populate the RecyclerView with
            ArrayList<String> animalNames = new ArrayList<>();
            animalNames.add("Horse");

            Button prev = findViewById(R.id.prevButton);
            prev.setEnabled(false);

            // set up the RecyclerView
            RecyclerView recyclerView = findViewById(R.id.rvPlaces);
            recyclerView.setLayoutManager(new LinearLayoutManager(this));
            //adapter = new MyRecyclerViewAdapter(this, animalNames);
            adapter = new MyRecyclerViewAdapter(this, message);
            adapter.setClickListener(this);
            recyclerView.setAdapter(adapter);

        } catch (JSONException e) {

        }

        final Button prev = findViewById(R.id.prevButton);
        final Button next = findViewById(R.id.nextButton);

        prev.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                JSONArray todo=null;
                switch (currentIndex){
                    case 0: return;
                    case 1: todo=message; break;
                    case 2: todo=message1; break;
                }

                // set up the RecyclerView
                RecyclerView recyclerView = findViewById(R.id.rvPlaces);
                recyclerView.setLayoutManager(new LinearLayoutManager(v.getContext()));
                //adapter = new MyRecyclerViewAdapter(this, animalNames);
                adapter = new MyRecyclerViewAdapter(v.getContext(), todo);
                adapter.setClickListener(ResultsActivity.this);
                recyclerView.setAdapter(adapter);

                currentIndex--;

                if(currentIndex==0){
                    prev.setEnabled(false);
                }

                next.setEnabled(true);

            }
        });

        next.setOnClickListener(new View.OnClickListener() {

            public void setadapter(JSONArray msg, View v){

                // set up the RecyclerView
                RecyclerView recyclerView = findViewById(R.id.rvPlaces);
                recyclerView.setLayoutManager(new LinearLayoutManager(v.getContext()));
                //adapter = new MyRecyclerViewAdapter(this, animalNames);
                adapter = new MyRecyclerViewAdapter(v.getContext(), msg);
                adapter.setClickListener(ResultsActivity.this);
                recyclerView.setAdapter(adapter);

            }

            public JSONArray getnextpage(String token, final int pos, final View v){

                final ProgressDialog pd = new ProgressDialog(v.getContext());
                pd.setMessage("Fetching Next Page");
                pd.show();


                RequestQueue queue = Volley.newRequestQueue(v.getContext());

                //token = "CrQCJgEAACjy9l7hjb2PcpLUi2A9GERfLTbYX4YSO1hpvvWkJflpWG1J2cIudAcjvICeRdHovDY5_Xpj7hg0w6vZ-e8Zyc3Z2r03ds5FzyNccgigykNyj0TikDthcJ2YQ7GRwcqXGtRbhxj-A7AV2pSWZ7qdPGs2uljh3qJmJPr7ZHkJOr1dImIfVp6UypJENDSvdE-e3J-RFXTqN7V6vTDlF-nEhrgNAWLZG-0ZvrT22jVUVtsaZOJM_Fe_ZLmrGHd7V6TROs9xxM17OXGW7UKVx7FXx0Z77RC4MZ6kSKh_ORw1fb2fLf_CXZMN535USv0oYIeJ18lZosYVUYWLuGqpPidjHG6R7z2co9Fr8ez7Ae0F7iGVDqA1hs-AO7E3OYxTKbhK43gWRsAI3YX0dTd4FbFh7BISEEBnbLCVde1L9SKJx0CmWVYaFLLO178dbM6hmhaxAAlKZFTS9gBH";
                String url = "http://node-express-env.am4vuh8cpm.us-west-1.elasticbeanstalk.com/getnextpage?pagetoken="+token;

                Log.d("myTag", url);

                // Request a string response from the provided URL.
                StringRequest stringRequest = new StringRequest(Request.Method.GET, url,
                        new Response.Listener<String>() {
                            @Override
                            public void onResponse(String response) {

                                Log.d("myTag", "done");

                                try {


                                    JSONObject js = new JSONObject(response);

                                    switch (pos){
                                        case 1:
                                            try{
                                                token2 = js.getString("next_page_token");
                                                Log.d("tokenTag",token2);
                                            }
                                            catch (JSONException e){
                                                token2="";
                                                next.setEnabled(false);
                                                Log.d("tokenTag","error for page2");
                                            }

                                            break;
                                    }

                                    switch (pos){

                                        case 1:{
                                            message1 = js.getJSONArray("results");
                                            break;
                                        }
                                        case 2:{
                                            message2 = js.getJSONArray("results");
                                            break;
                                        }

                                    }

                                    setadapter(js.getJSONArray("results"),v);

                                    pd.dismiss();


                                } catch (JSONException e) {
                                    Log.d("myTag", "h");

                                    pd.dismiss();
                                    Toast.makeText(v.getContext(),"Next page not yet generated by Google", Toast.LENGTH_SHORT).show();

                                }


                            }
                        }, new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {

                    }
                });

                queue.add(stringRequest);

                return null;
            }

            @Override
            public void onClick(View v) {


                JSONArray setmsg;
                String tk;
                switch (currentIndex){

                    case 0:{

                        if(token1.matches("")){
                            break;
                        }

                        if(message1!=null){
                            Log.d("tokenTag","using message1 previously stored");
                            setadapter(message1,v);

                            if(token2.matches("")){
                                next.setEnabled(false);
                            }

                        }
                        else{
                            Log.d("tokenTag","getting message1");
                            getnextpage(token1,1,v);
                        }


                        prev.setEnabled(true);
                        currentIndex++;

                        break;
                    }


                    case 1: {

                        Log.d("tokenTag","go to message2");

                        if(token2.matches("")){
                            Log.d("tokenTag","no token2");
                            break;
                        }

                        if(message2!=null){
                            Log.d("tokenTag","using message2 previously stored");
                            setadapter(message2,v);
                        }
                        else{
                            Log.d("tokenTag","getting message2");
                            getnextpage(token2,2,v);
                        }

                        next.setEnabled(false);


                        currentIndex++;

                        break;
                    }

                }

            }
        });

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
