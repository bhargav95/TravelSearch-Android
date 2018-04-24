package com.example.bhargav.travelsearch;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RatingBar;
import android.widget.TextView;

import com.squareup.picasso.Picasso;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

public class ReviewRecyclerViewAdapter extends RecyclerView.Adapter<ReviewRecyclerViewAdapter.ViewHolder> {

    private JSONArray mData= new JSONArray();
    private LayoutInflater mInflater;
    private ItemClickListener mClickListener;
    private Context context;
    private Boolean isYelp=false;

    // data is passed into the constructor
//    MyRecyclerViewAdapter(Context context, List<String> data) {
//        this.mInflater = LayoutInflater.from(context);
//        this.mData = data;
//    }

    ReviewRecyclerViewAdapter(JSONArray data, Boolean yelpornot){
        this.mData =  data;

        this.isYelp = yelpornot;

    }

    public JSONObject getobj(int pos){

        try {
            return mData.getJSONObject(pos);
        } catch (JSONException e) {
            e.printStackTrace();
        }

        return null;
    }

    public String getanimal(JSONObject animal, String key){

        try {
            return animal.get(key).toString();
            //Log.d("myTag",animal.get(key).toString());
        } catch (JSONException e) {
            e.printStackTrace();
        }

        return null;
    }

    // inflates the row layout from xml when needed
    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.reviewview_row, parent, false);

        return new ViewHolder(view);
    }

    // binds the data to the TextView in each row
    @Override
    public void onBindViewHolder(final ViewHolder holder, int position) {

        final JSONObject animal = getobj(position);

        if(isYelp){
            Log.d("yelpTag",String.valueOf(position));
            try {
                holder.nameTextView.setText(animal.getJSONObject("user").getString("name"));
            } catch (JSONException e) {
                e.printStackTrace();
            }
            holder.dateTextView.setText(getanimal(animal,"time_created"));
            holder.reviewTextView.setText(getanimal(animal,"text"));
            holder.reviewRatingBar.setRating(Float.parseFloat(getanimal(animal,"rating")));
            //holder.myImageView=new LoaderImageView((animal.get("icon")));
            try {
                Picasso.with(context).load(animal.getJSONObject("user").getString("image_url")).into(holder.avatarImageView);
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
        else{
            holder.nameTextView.setText(getanimal(animal,"author_name"));
            holder.dateTextView.setText(getanimal(animal,"time"));
            holder.reviewTextView.setText(getanimal(animal,"text"));
            holder.reviewRatingBar.setRating(Float.parseFloat(getanimal(animal,"rating")));
            //holder.myImageView=new LoaderImageView((animal.get("icon")));
            Picasso.with(context).load(getanimal(animal,"profile_photo_url")).into(holder.avatarImageView);
        }

    }

    // total number of rows
    @Override
    public int getItemCount() {
        return mData.length();
    }


    // stores and recycles views as they are scrolled off screen
    public class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        TextView nameTextView;
        TextView dateTextView;
        TextView reviewTextView;
        ImageView avatarImageView;
        RatingBar reviewRatingBar;


        ViewHolder(View itemView) {
            super(itemView);
            nameTextView = itemView.findViewById(R.id.nameTextView);
            dateTextView = itemView.findViewById(R.id.dateTextView);
            reviewTextView = itemView.findViewById(R.id.reviewTextView);
            avatarImageView = itemView.findViewById(R.id.avatarImageView);
            reviewRatingBar = itemView.findViewById(R.id.reviewRatingBar);
            itemView.setOnClickListener(this);
        }

        @Override
        public void onClick(View view) {
            if (mClickListener != null) mClickListener.onItemClick(view, getAdapterPosition());
        }
    }

    // convenience method for getting data at click position
    public JSONObject getItem(int id) {
        try{
            return mData.getJSONObject(id);
        }
        catch(JSONException e){
            return new JSONObject();
        }
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