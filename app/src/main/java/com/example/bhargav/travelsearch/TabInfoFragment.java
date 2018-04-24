package com.example.bhargav.travelsearch;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RatingBar;
import android.widget.TextView;

import org.json.JSONException;
import org.json.JSONObject;

public class TabInfoFragment extends Fragment {

    private JSONObject js;

    public String getTheThing(String key){

        try {
            return js.getJSONObject("result").get(key).toString();
        } catch (JSONException e) {
            e.printStackTrace();
            Log.d("myTag",key);
        }

        return "";
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment

        View vv = inflater.inflate(R.layout.tabinfo_fragment, container, false);

        js = ((PlaceDetailsActivity)this.getActivity()).res;

        //TextView ittv = vv.findViewById(R.id.infoTabTextView);

        //ittv.setText(js.toString());

        Log.d("myTag",js.toString());

        TextView address = vv.findViewById(R.id.addrTextView);
        TextView phno = vv.findViewById(R.id.phnoTextView);
        TextView gp = vv.findViewById(R.id.gpTextView);
        TextView wb = vv.findViewById(R.id.wbTextView);
        TextView price = vv.findViewById(R.id.dollarTextView);
        RatingBar rt = vv.findViewById(R.id.infoRatingBar);

        String pl = getTheThing("price_level");

        if(!pl.matches("")){
            Integer x = Integer.parseInt(getTheThing("price_level"));

            StringBuilder dollar = new StringBuilder();

            for(int i=0;i<x;++i){
                dollar.append("$");
            }

            pl = dollar.toString();

        }

        price.setText(pl);
        address.setText(getTheThing("formatted_address"));
        phno.setText(getTheThing("international_phone_number"));

        if(!getTheThing("rating").matches(""))
            rt.setRating(Float.parseFloat(getTheThing("rating")));

        gp.setText(getTheThing("url"));

        wb.setText(getTheThing("website"));


        return vv;
    }
}
