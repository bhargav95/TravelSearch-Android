package com.example.bhargav.travelsearch;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;
import android.support.design.widget.TabLayout;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.widget.Toast;

import org.json.JSONException;
import org.json.JSONObject;

public class PlaceDetailsActivity extends AppCompatActivity {

    private static final String TAG = "PlaceDetailsActivity";

    JSONObject res;
    Intent intent;
    Integer pos;
    private SharedPreferences sharedPref;

    public JSONObject getres() {

        try {
            JSONObject message = new JSONObject(intent.getStringExtra(ResultsActivity.PLACE_MESSAGE));
            return message;
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return null;
    }

    private SectionsPageAdapter mSectionsPageAdapter;

    private ViewPager mViewPager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_place_details);
        Log.d(TAG, "onCreate: Starting.");

        intent = getIntent();
        res = getres();
        pos = Integer.valueOf(intent.getStringExtra(ResultsActivity.POSITION_MESSAGE));

        mSectionsPageAdapter = new SectionsPageAdapter(getSupportFragmentManager());

        // Set up the ViewPager with the sections adapter.
        mViewPager = (ViewPager) findViewById(R.id.containerPlace);
        setupViewPager(mViewPager);

        TabLayout tabLayout = (TabLayout) findViewById(R.id.tabsPlace);
        tabLayout.setupWithViewPager(mViewPager);
        tabLayout.getTabAt(0).setIcon(R.drawable.info_outline);
        tabLayout.getTabAt(1).setIcon(R.drawable.photos);
        tabLayout.getTabAt(2).setIcon(R.drawable.maps);
        tabLayout.getTabAt(3).setIcon(R.drawable.review);

        try {
            setTitle(res.getJSONObject("result").getString("name"));
        } catch (JSONException e) {
            e.printStackTrace();
        }
        getSupportActionBar().setHomeButtonEnabled(true);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        sharedPref = getSharedPreferences("Favs", Context.MODE_PRIVATE);
    }

    private void setupViewPager(ViewPager viewPager) {
        SectionsPageAdapter adapter = new SectionsPageAdapter(getSupportFragmentManager());
        adapter.addFragment(new TabInfoFragment(), "Info");
        adapter.addFragment(new TabPhotosFragment(), "Photos");
        adapter.addFragment(new TabMapFragment(), "Map");
        adapter.addFragment(new TabReviewsFragment(), "Reviews");
        viewPager.setAdapter(adapter);
    }

    @Override
    public boolean onSupportNavigateUp() {
        finish();
        return true;
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu items for use in the action bar
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.twitter_fav, menu);

        try {
            String placeid = res.getJSONObject("result").getString("place_id");

            if(sharedPref.contains(placeid)){

                menu.findItem(R.id.FavActionBarIcon).setIcon(R.drawable.heart_fill_white);

            }

        } catch (JSONException e) {
            e.printStackTrace();
        }


        return super.onCreateOptionsMenu(menu);
    }

    public void openTwitter() {

        String name = "";
        try {
            name = res.getJSONObject("result").getString("name");
        } catch (JSONException e) {
            e.printStackTrace();
        }
        String location = "";
        try {
            location = res.getJSONObject("result").getString("vicinity");
        } catch (JSONException e) {
            e.printStackTrace();
        }
        String website = "";
        try {
            website = res.getJSONObject("result").getString("website");
        } catch (JSONException e) {
            e.printStackTrace();
        }

        String twitterUrl = "https://twitter.com/intent/tweet?text=";
        String text = "Check out " + name + " located at " + location + ". Website: &url=";
        String url = website + "&hashtags=TravelAndEntertainmentSearch";

        twitterUrl += text + url;

        Intent browserIntent = new Intent(Intent.ACTION_VIEW, Uri.parse(twitterUrl));
        startActivity(browserIntent);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle presses on the action bar items
        switch (item.getItemId()) {
            case R.id.twitterIcon:
                openTwitter();
                return true;
            case R.id.FavActionBarIcon:
                //openSettings();

                String placeid = "";
                String placename = "";
                try {
                    placeid = res.getJSONObject("result").getString("place_id");
                    placename = res.getJSONObject("result").getString("name");

                    if (sharedPref.getString(placeid, "").matches("")) {
                        item.setIcon(R.drawable.heart_fill_white);

                        SharedPreferences.Editor editor = sharedPref.edit();

                        editor.putString(placeid, res.getJSONObject("result").toString());
                        editor.commit();

                        try{
                            ResultsActivity.adapter.notifyDataSetChanged();
                        }
                        catch(NullPointerException e){

                        }

                        try{
                            TabFavFragment.adapter.notifyDataSetChanged();
                        }
                        catch (NullPointerException e){

                        }

                        Toast.makeText(this, placename + " added to favorites", Toast.LENGTH_SHORT).show();

                    } else {
                        item.setIcon(R.drawable.heart_outline_white);

                        SharedPreferences.Editor editor = sharedPref.edit();
                        editor.remove(placeid);
                        editor.commit();

                        try{
                            ResultsActivity.adapter.notifyDataSetChanged();
                        }
                        catch(NullPointerException e){

                        }

                        try{
                            TabFavFragment.adapter.notifyDataSetChanged();
                        }
                        catch (NullPointerException e){

                        }



                        Toast.makeText(this, placename + " removed from favorites", Toast.LENGTH_SHORT).show();
                    }


                } catch (JSONException e) {

                    Log.d("myTag", res.toString());

                    e.printStackTrace();
                }

                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

}