package com.example.bhargav.travelsearch;

import android.graphics.Bitmap;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.google.android.gms.location.places.GeoDataClient;
import com.google.android.gms.location.places.PlacePhotoMetadata;
import com.google.android.gms.location.places.PlacePhotoMetadataBuffer;
import com.google.android.gms.location.places.PlacePhotoMetadataResponse;
import com.google.android.gms.location.places.PlacePhotoResponse;
import com.google.android.gms.location.places.Places;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;

import org.json.JSONException;
import org.json.JSONObject;

public class TabPhotosFragment extends Fragment {

    private GeoDataClient mGeoDataClient;
    private Bitmap bitmap;
    JSONObject js;

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

        final Task<PlacePhotoMetadataResponse> photoMetadataResponse = mGeoDataClient.getPlacePhotos(placeId);
        photoMetadataResponse.addOnCompleteListener(new OnCompleteListener<PlacePhotoMetadataResponse>() {
            @Override
            public void onComplete(@NonNull Task<PlacePhotoMetadataResponse> task) {
                // Get the list of photos.
                PlacePhotoMetadataResponse photos = task.getResult();

                // Get the PlacePhotoMetadataBuffer (metadata for all of the photos).
                PlacePhotoMetadataBuffer photoMetadataBuffer = photos.getPhotoMetadata();
                // Get the first photo in the list.

                Log.d("photoTag",photoMetadataBuffer.toString());

                try{
                    PlacePhotoMetadata photoMetadata = photoMetadataBuffer.get(0);
                    // Get the attribution text.
                    //CharSequence attribution = photoMetadata.getAttributions();

                    // Get a full-size bitmap for the photo.
                    Task<PlacePhotoResponse> photoResponse = mGeoDataClient.getPhoto(photoMetadata);
                    photoResponse.addOnCompleteListener(new OnCompleteListener<PlacePhotoResponse>() {
                        @Override
                        public void onComplete(@NonNull Task<PlacePhotoResponse> task) {
                            PlacePhotoResponse photo = task.getResult();
                            bitmap = photo.getBitmap();

                            ImageView photograph = vv.findViewById(R.id.imageView0);
                            photograph.setImageBitmap(bitmap);
                        }
                    });
                }
                catch (Exception e){

                }

            }
        });
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
