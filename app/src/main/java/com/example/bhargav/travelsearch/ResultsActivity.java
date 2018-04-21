package com.example.bhargav.travelsearch;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONException;

import java.util.ArrayList;

public class ResultsActivity extends AppCompatActivity implements MyRecyclerViewAdapter.ItemClickListener{

    MyRecyclerViewAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_results);



        // Get the Intent that started this activity and extract the string
        Intent intent = getIntent();
        try{
            JSONArray message = new JSONArray(intent.getStringExtra(MainActivity.EXTRA_MESSAGE));

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

        }
        catch(JSONException e){

        }

    }

    @Override
    public void onItemClick(View view, int position) {
        Toast.makeText(this, "You clicked " + adapter.getItem(position) + " on row number " + position, Toast.LENGTH_SHORT).show();
    }
}
