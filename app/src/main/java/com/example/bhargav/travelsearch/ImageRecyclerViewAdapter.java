package com.example.bhargav.travelsearch;

import android.graphics.Bitmap;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.google.android.gms.location.places.GeoDataClient;
import com.google.android.gms.location.places.PlacePhotoMetadata;
import com.google.android.gms.location.places.PlacePhotoResponse;
import com.google.android.gms.location.places.Places;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;

import java.util.List;


public class ImageRecyclerViewAdapter extends RecyclerView.Adapter<ImageRecyclerViewAdapter.ViewHolder> {
    private List<PlacePhotoMetadata> photosDataList;

    private GeoDataClient mGeoDataClient;

    private String TAG = "myTag";

    public ImageRecyclerViewAdapter(List<PlacePhotoMetadata> itemsData) {
        this.photosDataList = itemsData;
    }

    @Override
    public ImageRecyclerViewAdapter.ViewHolder onCreateViewHolder(ViewGroup parent,
                                                                    int viewType) {
        View itemLayoutView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.imagerecyclerview_row, null);

        mGeoDataClient = Places.getGeoDataClient(parent.getContext(),null);
        ViewHolder viewHolder = new ViewHolder(itemLayoutView);
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(ViewHolder viewHolder, int position) {

        // photosDataList.get(position)
        // viewHolder.imgViewIcon
        // setPhoto(viewHolder.imgViewIcon, position)

        setPhoto(viewHolder.imgViewIcon,photosDataList.get(position));

        //viewHolder.imgViewIcon.setImageBitmap(photosDataList[position].getImageUrl());
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {

        public ImageView imgViewIcon;

        public ViewHolder(View itemLayoutView) {
            super(itemLayoutView);
            imgViewIcon= itemLayoutView.findViewById(R.id.imagerow);
        }
    }

    private void setPhoto(final ImageView img, PlacePhotoMetadata photoMetadata){
        Task<PlacePhotoResponse> photoResponse = mGeoDataClient.getPhoto(photoMetadata);
        photoResponse.addOnCompleteListener(new OnCompleteListener<PlacePhotoResponse>() {
            @Override
            public void onComplete(@NonNull Task<PlacePhotoResponse> task) {
                PlacePhotoResponse photo = task.getResult();
                Bitmap photoBitmap = photo.getBitmap();
                Log.d(TAG, "photo "+photo.toString());

                //placeImage.invalidate();
                img.setImageBitmap(photoBitmap);
            }
        });
    }



    @Override
    public int getItemCount() {
        return photosDataList.size();
    }
}
