package com.example.bhargav.travelsearch;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.Spinner;

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
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

public class TabReviewsFragment extends Fragment implements ReviewRecyclerViewAdapter.ItemClickListener {

    private ReviewRecyclerViewAdapter adapter;
    private JSONObject js;
    private JSONArray reviews=new JSONArray();
    private JSONArray yelp=new JSONArray();
    private Boolean isYelp=false;
    private JSONArray yelpnewreviews2, newreviews2;

    public String getTheCoordinate(JSONObject jj, String key){

        try {
            return jj.getJSONObject("result").getJSONObject("geometry").getJSONObject("location").get(key).toString();
        } catch (JSONException e) {
            e.printStackTrace();
            return "";
        }
    }

    public String getTheThing(JSONObject jj, String key){

        try {
            return jj.getJSONObject("result").get(key).toString();
        } catch (JSONException e) {
            e.printStackTrace();
            return "";
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        final View vv = inflater.inflate(R.layout.tabreviews_fragment, container, false);
        js = ((PlaceDetailsActivity)this.getActivity()).res;

        try {
            reviews = js.getJSONObject("result").getJSONArray("reviews");
        } catch (JSONException e) {
            e.printStackTrace();
        }

        //YELP

        String lat = getTheCoordinate(js,"lat");
        String lng = getTheCoordinate(js,"lng");
        String address1 = getTheThing(js, "vicinity");
        String name = getTheThing(js,"name");
        String city="",country="", state="";

        JSONArray json = null;
        try {
            json = js.getJSONObject("result").getJSONArray("address_components");

            for(int i=0;i<json.length();++i){

                JSONObject acomp = json.getJSONObject(i);

                JSONArray types = acomp.getJSONArray("types");

                for(int j=0;j<types.length();++j){

                    switch (types.getString(j)){

                        case "country":{
                            country = acomp.getString("short_name");
                            break;
                        }
                        case "administrative_area_level_1":{
                            state = acomp.getString("short_name");
                            break;
                        }
                        case "locality":{
                            city = acomp.getString("short_name");
                        }

                    }

                }

            }

        } catch (JSONException e) {
            e.printStackTrace();
        }

        Log.d("sortTag",city);
        Log.d("sortTag",state);
        Log.d("sortTag",country);

        //YELP CALL

        String url = "http://node-express-env.am4vuh8cpm.us-west-1.elasticbeanstalk.com/yelpsearch?address1="+address1+"&city="+city+"&state="+state+"&country="+country
                +"&latitude="+lat+"&longitude="+lng+"&name="+name;

        Log.d("sortTag",url);

        RequestQueue queue = Volley.newRequestQueue(vv.getContext());

        // Request a string response from the provided URL.
        StringRequest stringRequest = new StringRequest(Request.Method.GET, url,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {

                        try {
                            JSONObject js = new JSONObject(response);

                            yelp = js.getJSONArray("reviews");
                            yelpnewreviews2 = yelp;

                            Log.d("yelpTag",String.valueOf(yelp.length()));

                            // set up the RecyclerView
                            final RecyclerView recyclerView = vv.findViewById(R.id.rvReviews);
                            recyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
                            //adapter = new MyRecyclerViewAdapter(this, animalNames);
                            adapter = new ReviewRecyclerViewAdapter(reviews, false);
                            adapter.setClickListener(TabReviewsFragment.this);
                            recyclerView.setAdapter(adapter);

                        } catch (JSONException e) {
                            e.printStackTrace();
                        }

                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {

            }
        });

        queue.add(stringRequest);

        Spinner googleoryelp = vv.findViewById(R.id.reviewSpinner);
        final Spinner sorter = vv.findViewById(R.id.sortSpinner);

        sorter.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {

            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {


                final int sortby = position;

                final String [] sortstring={"","rating","rating","time","time"};
                final Boolean[] rev = {false,true,false,true,false};

                final String [] yelpsortstring={"","rating","rating","time_created","time_created"};
                final Boolean[] yelprev = {false,true,false,true,false};


                //GOOGLE
                List<JSONObject> newreviews = new ArrayList<JSONObject>();
                for (int i = 0; i < reviews.length(); i++) {
                    try {
                        newreviews.add(reviews.getJSONObject(i));
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }

                Collections.sort(newreviews, new Comparator<JSONObject>() {

                    public int compare(JSONObject a, JSONObject b)
                    {
                        //valA and valB could be any simple type, such as number, string, whatever
                        String valA = "";
                        try {
                            valA = a.get(sortstring[sortby]).toString();
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                        String valB = "";
                        try {
                            valB = b.get(sortstring[sortby]).toString();
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }

                        if(rev[sortby])
                            return valB.compareTo(valA);
                        else
                            return valA.compareTo(valB);
                        //if your value is numeric:
                        //if(valA > valB)
                        //    return 1;
                        //if(valA < valB)
                        //    return -1;
                        //return 0;
                    }
                });

                newreviews2 = new JSONArray(newreviews);
                //ENDGOOGLE

                //YELP
                List<JSONObject> yelpnewreviews = new ArrayList<JSONObject>();
                for (int i = 0; i < yelp.length(); i++) {
                    try {
                        yelpnewreviews.add(yelp.getJSONObject(i));
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }

                Collections.sort(yelpnewreviews, new Comparator<JSONObject>() {

                    public int compare(JSONObject a, JSONObject b)
                    {
                        //valA and valB could be any simple type, such as number, string, whatever
                        String valA = "";
                        try {
                            valA = a.get(yelpsortstring[sortby]).toString();
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                        String valB = "";
                        try {
                            valB = b.get(yelpsortstring[sortby]).toString();
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }

                        if(yelprev[sortby])
                            return valB.compareTo(valA);
                        else
                            return valA.compareTo(valB);
                        //if your value is numeric:
                        //if(valA > valB)
                        //    return 1;
                        //if(valA < valB)
                        //    return -1;
                        //return 0;
                    }
                });

                yelpnewreviews2 = new JSONArray(yelpnewreviews);
                //ENDYELP


                final RecyclerView recyclerView = vv.findViewById(R.id.rvReviews);
                recyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
                //adapter = new MyRecyclerViewAdapter(this, animalNames);

                if(isYelp)
                    adapter = new ReviewRecyclerViewAdapter(yelpnewreviews2, true);
                else
                    adapter = new ReviewRecyclerViewAdapter(newreviews2, false);

                adapter.setClickListener(TabReviewsFragment.this);
                recyclerView.setAdapter(adapter);
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

        googleoryelp.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {

                if(position==0){
                    isYelp=false;
                }
                else{
                    isYelp=true;
                }
                final RecyclerView recyclerView = vv.findViewById(R.id.rvReviews);
                recyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
                //adapter = new MyRecyclerViewAdapter(this, animalNames);

                if(isYelp)
                    //adapter = new ReviewRecyclerViewAdapter(yelp, true);
                    adapter = new ReviewRecyclerViewAdapter(yelpnewreviews2, true);
                else
                    adapter = new ReviewRecyclerViewAdapter(newreviews2, false);
                adapter.setClickListener(TabReviewsFragment.this);
                recyclerView.setAdapter(adapter);

            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

        return vv;
    }


    @Override
    public void onItemClick(View view, int position) {

        JSONObject js = adapter.getItem(position);

        //Log.d("urlTag",js.toString());

        String url="";

        if(!isYelp){
            try {
                url = js.get("author_url").toString();
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
        else{
            try {
                url = js.get("url").toString();
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }

        //Log.d("urlTag","url:"+url);

        Intent browserIntent = new Intent(Intent.ACTION_VIEW, Uri.parse(url));
        startActivity(browserIntent);
    }
}
