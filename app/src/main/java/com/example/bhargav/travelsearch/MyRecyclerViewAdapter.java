package com.example.bhargav.travelsearch;

import android.content.Context;
import android.content.SharedPreferences;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.squareup.picasso.Picasso;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

public class MyRecyclerViewAdapter extends RecyclerView.Adapter<MyRecyclerViewAdapter.ViewHolder> {

    private JSONArray mData;
    private LayoutInflater mInflater;
    private ItemClickListener mClickListener;
    private Context context;

    private SharedPreferences sharedPref;

    // data is passed into the constructor
//    MyRecyclerViewAdapter(Context context, List<String> data) {
//        this.mInflater = LayoutInflater.from(context);
//        this.mData = data;
//    }

    MyRecyclerViewAdapter(Context context, JSONArray data) {
        this.mInflater = LayoutInflater.from(context);
        this.mData = data;
        this.context = context;

        this.setHasStableIds(true);
    }

    // inflates the row layout from xml when needed
    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = mInflater.inflate(R.layout.recyclerview_row, parent, false);

        sharedPref = view.getContext().getSharedPreferences("Favs", Context.MODE_PRIVATE);
        return new ViewHolder(view);
    }

    private JSONObject getobj(int pos) {
        try {
            return mData.getJSONObject(pos);
        } catch (JSONException e) {
            e.printStackTrace();
        }

        return null;
    }

    private String getanimal(JSONObject a, String key) {

        try {
            return a.get(key).toString();
        } catch (JSONException e) {
            e.printStackTrace();
        }

        return null;
    }

    // binds the data to the TextView in each row
    @Override
    public void onBindViewHolder(final ViewHolder holder, int position) {

        final JSONObject animal = getobj(position);
        final String placeid = getanimal(animal, "place_id");
        final String placename = getanimal(animal, "name");


        try {

            holder.myTextView2.setText(animal.get("vicinity").toString());
            holder.myTextView1.setText(animal.get("name").toString());
            //holder.myImageView=new LoaderImageView((animal.get("icon")));
            Picasso.with(context).load(animal.get("icon").toString()).into(holder.myImageView);

            if (sharedPref.contains(animal.get("place_id").toString())) {
                holder.favicon.setImageResource(R.drawable.heart_fill_red);

            } else {
                holder.favicon.setImageResource(R.drawable.heart_outline_black);

            }
        } catch (JSONException e) {

            e.printStackTrace();
        }

        holder.favicon.setOnClickListener(
                new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {

                        Log.d("myTag", String.valueOf(sharedPref.getString(placeid, "not found")));

                        if (sharedPref.getString(placeid, "").matches("")) {

                            SharedPreferences.Editor editor = sharedPref.edit();

                            editor.putString(placeid, animal.toString());
                            editor.commit();

                            holder.favicon.setImageResource(R.drawable.heart_fill_red);


                            Toast.makeText(v.getContext(), placename + " added to favorites", Toast.LENGTH_SHORT).show();

                        } else {
                            SharedPreferences.Editor editor = sharedPref.edit();
                            editor.remove(placeid);
                            editor.commit();
                            holder.favicon.setImageResource(R.drawable.heart_outline_black);


                            ResultsActivity.adapter.notifyDataSetChanged();

                            Toast.makeText(v.getContext(), placename + " removed from favorites", Toast.LENGTH_SHORT).show();

                        }

                    }
                });
    }

    // total number of rows
    @Override
    public int getItemCount() {
        return mData.length();
    }


    // stores and recycles views as they are scrolled off screen
    public class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        TextView myTextView1;
        TextView myTextView2;
        ImageView myImageView;
        ImageView favicon;

        ViewHolder(View itemView) {
            super(itemView);
            myTextView1 = itemView.findViewById(R.id.firstLine);
            myTextView2 = itemView.findViewById(R.id.secondLine);
            myImageView = itemView.findViewById(R.id.imagerow);
            favicon = itemView.findViewById(R.id.favicon);
            itemView.setOnClickListener(this);
        }

        @Override
        public void onClick(View view) {
            if (mClickListener != null) mClickListener.onItemClick(view, getAdapterPosition());
        }
    }

    // convenience method for getting data at click position
    public JSONObject getItem(int id) {
        try {
            return mData.getJSONObject(id);
        } catch (JSONException e) {
            return new JSONObject();
        }
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public int getItemViewType(int position) {
        return position;
    }

    // allows clicks events to be caught
    void setClickListener(ItemClickListener itemClickListener) {
        this.mClickListener = itemClickListener;
    }

    // parent activity will implement this method to respond to click events
    public interface ItemClickListener {
        void onItemClick(View view, int position);
    }
}