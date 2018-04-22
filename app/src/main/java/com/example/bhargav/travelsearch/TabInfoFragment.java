package com.example.bhargav.travelsearch;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import org.json.JSONObject;

public class TabInfoFragment extends Fragment {
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment

        View vv = inflater.inflate(R.layout.tabinfo_fragment, container, false);

        JSONObject js = ((PlaceDetailsActivity)this.getActivity()).res;

        TextView ittv = vv.findViewById(R.id.infoTabTextView);

        ittv.setText(js.toString());

        return vv;
    }
}
