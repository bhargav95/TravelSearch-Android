package com.example.bhargav.travelsearch;

import android.graphics.Bitmap;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.google.android.gms.location.places.GeoDataClient;
import com.google.android.gms.location.places.PlacePhotoMetadata;
import com.google.android.gms.location.places.PlacePhotoMetadataBuffer;
import com.google.android.gms.location.places.PlacePhotoMetadataResponse;
import com.google.android.gms.location.places.Places;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

public class TabPhotosFragment extends Fragment {

    private GeoDataClient mGeoDataClient;
    private Bitmap bitmap;
    JSONObject js;
    ImageRecyclerViewAdapter adapter;

    private List<PlacePhotoMetadata> photosDataList;
    private int currentPhotoIndex = 0;

    // Request photos and metadata for the specified place.
    private void getPhotos(final View vv) {

        js = ((PlaceDetailsActivity)this.getActivity()).res;


        String placeId="";
        try {
            placeId = js.getJSONObject("result").get("place_id").toString();
        } catch (JSONException e) {
            e.printStackTrace();
            return;
        }

        Log.d("myTag",placeId);

        final Task<PlacePhotoMetadataResponse> photoResponse =
                mGeoDataClient.getPlacePhotos(placeId);

        photoResponse.addOnCompleteListener
                (new OnCompleteListener<PlacePhotoMetadataResponse>() {
                    @Override
                    public void onComplete(@NonNull Task<PlacePhotoMetadataResponse> task) {
                        currentPhotoIndex = 0;
                        photosDataList = new ArrayList<>();
                        PlacePhotoMetadataResponse photos = task.getResult();
                        PlacePhotoMetadataBuffer photoMetadataBuffer = photos.getPhotoMetadata();

                        Log.d("myTag", "number of photos "+photoMetadataBuffer.getCount());

                        int count=0;
                        for(PlacePhotoMetadata photoMetadata : photoMetadataBuffer){
                            photosDataList.add(photoMetadataBuffer.get(count).freeze());
                            ++count;
                        }

                        photoMetadataBuffer.release();


                        // set up the RecyclerView
                        RecyclerView recyclerView = vv.findViewById(R.id.rvImage);
                        recyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
                        //adapter = new MyRecyclerViewAdapter(this, animalNames);
                        adapter = new ImageRecyclerViewAdapter(photosDataList);

                        recyclerView.setAdapter(adapter);
                    }
                });



//        final Task<PlacePhotoMetadataResponse> photoMetadataResponse = mGeoDataClient.getPlacePhotos(placeId);
//        photoMetadataResponse.addOnCompleteListener(new OnCompleteListener<PlacePhotoMetadataResponse>() {
//            @Override
//            public void onComplete(@NonNull Task<PlacePhotoMetadataResponse> task) {
//                // Get the list of photos.
//                PlacePhotoMetadataResponse photos = task.getResult();
//
//                // Get the PlacePhotoMetadataBuffer (metadata for all of the photos).
//                PlacePhotoMetadataBuffer photoMetadataBuffer = photos.getPhotoMetadata();
//                // Get the first photo in the list.
//
//                Log.d("photoTag",photoMetadataBuffer.toString());
//
//
//                try{
//                    PlacePhotoMetadata photoMetadata = photoMetadataBuffer.get(0);
//                    // Get the attribution text.
//                    //CharSequence attribution = photoMetadata.getAttributions();
//
//                    // Get a full-size bitmap for the photo.
//                    Task<PlacePhotoResponse> photoResponse = mGeoDataClient.getPhoto(photoMetadata);
//                    photoResponse.addOnCompleteListener(new OnCompleteListener<PlacePhotoResponse>() {
//                        @Override
//                        public void onComplete(@NonNull Task<PlacePhotoResponse> task) {
//                            PlacePhotoResponse photo = task.getResult();
//                            bitmap = photo.getBitmap();
//
//                            ImageView photograph = vv.findViewById(R.id.imageView0);
//                            photograph.setImageBitmap(bitmap);
//                        }
//                    });
//                }
//                catch (Exception e){
//
//                }
//
//            }
//        });
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment

        View vv = inflater.inflate(R.layout.tabphotos_fragment, container, false);

        mGeoDataClient = Places.getGeoDataClient(getContext(),null);
        getPhotos(vv);

        return vv;
    }
}
