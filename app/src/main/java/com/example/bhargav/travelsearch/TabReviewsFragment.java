package com.example.bhargav.travelsearch;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

public class TabReviewsFragment extends Fragment {

    private ReviewRecyclerViewAdapter adapter;
    private JSONObject js;
    private JSONArray reviews=new JSONArray();

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment

        js = ((PlaceDetailsActivity)this.getActivity()).res;

        try {
            reviews = js.getJSONObject("result").getJSONArray("reviews");
        } catch (JSONException e) {
            e.printStackTrace();
        }


        View vv = inflater.inflate(R.layout.tabreviews_fragment, container, false);

        // set up the RecyclerView
        RecyclerView recyclerView = vv.findViewById(R.id.rvReviews);
        recyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
        //adapter = new MyRecyclerViewAdapter(this, animalNames);
        adapter = new ReviewRecyclerViewAdapter(reviews);

        recyclerView.setAdapter(adapter);

        return vv;
    }
}
