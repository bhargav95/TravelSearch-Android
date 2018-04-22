package com.example.bhargav.travelsearch;

import android.content.Context;
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

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Map;

/**
 * Created by User on 2/28/2017.
 */

public class TabFavFragment extends Fragment {
    private static final String TAG = "Tab2Fragment";
    MyFavViewAdapter adapter;

    private Button btnTEST;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.tab2_fragment,container,false);


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

        TextView nofavtext =getView().findViewById(R.id.textViewFav);
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

        //adapter.setClickListener(this);

        recyclerView.setAdapter(adapter);
    }
}
