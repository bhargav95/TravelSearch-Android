package com.example.bhargav.travelsearch;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
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
import java.util.Map;

/**
 * Created by User on 2/28/2017.
 */

public class TabFavFragment extends Fragment implements MyFavViewAdapter.ItemClickListener{
    private static final String TAG = "Tab2Fragment";
    public static MyFavViewAdapter adapter;
    private static View view;
    public static final String PLACE_MESSAGE = "com.example.bhargav.travelsearch.PLACE_MESSAGE";
    public static final String POSITION_MESSAGE = "com.example.bhargav.travelsearch.POSITION_MESSAGE";

    private Button btnTEST;

    public static void showNo(){

        TextView fav = TabFavFragment.view.findViewById(R.id.noFavTextView);

        fav.setVisibility(View.VISIBLE);
        fav.setText("No favorites");

    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.tab2_fragment,container,false);


        return view;
    }

    @Override
    public void onResume() {
        super.onResume();

        SharedPreferences sharedPref = getContext().getSharedPreferences("Favs", Context.MODE_PRIVATE);

        Map<String,?> en = sharedPref.getAll();

        boolean nofavs = true;


        JSONArray message= new JSONArray();

        for(Map.Entry<String,?> entry: en.entrySet())
        {
            Log.d(TAG, "JSON value obtained:" );
            Log.d(TAG, entry.getValue().toString());
            nofavs= false;
            String jsonData = entry.getValue().toString();

            try {
                JSONObject js = new JSONObject(jsonData);
                message.put(js);
            } catch (JSONException e) {
                e.printStackTrace();
            }

        }

        TextView nofavtext =getView().findViewById(R.id.noFavTextView);
        if(nofavs==true){
            nofavtext.setText("No Favorites");
        }
        else{
            nofavtext.setText("");
        }

        // Capture the layout's TextView and set the string as its text
        //TextView textView = findViewById(R.id.resultTextView);
        //textView.setText(message);

        // data to populate the RecyclerView with
        ArrayList<String> animalNames = new ArrayList<>();
        animalNames.add("Horse");

        // set up the RecyclerView
        RecyclerView recyclerView = getView().findViewById(R.id.rvFav);
        recyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
        //adapter = new MyRecyclerViewAdapter(this, animalNames);
        adapter = new MyFavViewAdapter(getContext(), message);

        adapter.setClickListener(this);

        recyclerView.setAdapter(adapter);
    }

    @Override
    public void onItemClick(final View view, final int position) {

        final ProgressDialog pd = new ProgressDialog(getActivity());
        pd.setMessage("Fetching Place Info");
        pd.show();

        String place_id = "";
        try {
            place_id = adapter.getItem(position).get("place_id").toString();

            Log.d("myTag",place_id);

        } catch (JSONException e) {
            e.printStackTrace();
        }

        RequestQueue queue = Volley.newRequestQueue(getActivity());

        String url = "http://my-cloned-env.us-west-1.elasticbeanstalk.com/placeinfo?placeid=" + place_id;
        //String url = "http://my-cloned-env-vasuki.us-west-2.elasticbeanstalk.com/placedetails?placeid=" + place_id;

        Log.d("myTag", url);

        // Request a string response from the provided URL.
        StringRequest stringRequest = new StringRequest(Request.Method.GET, url,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {

                        Log.d("myTag", "done");

                        try {


                            JSONObject js = new JSONObject(response);

                            Intent intent = new Intent(view.getContext(), PlaceDetailsActivity.class);
                            intent.putExtra(PLACE_MESSAGE, js.toString());
                            intent.putExtra(POSITION_MESSAGE, String.valueOf(position));
                            startActivity(intent);

                            pd.dismiss();


                        } catch (JSONException e) {
                            Log.d("myTag", "h");

                            pd.dismiss();
                            Toast.makeText(view.getContext(), "Error getting place info, please try again", Toast.LENGTH_SHORT).show();
                        }


                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {

                pd.dismiss();
                Toast.makeText(view.getContext(), "Network Error, please try again", Toast.LENGTH_SHORT).show();
            }
        });

        queue.add(stringRequest);
    }
}
