package com.example.bhargav.travelsearch;

import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.TabLayout;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;

import org.json.JSONException;
import org.json.JSONObject;

public class PlaceDetailsActivity extends AppCompatActivity {

    private static final String TAG = "PlaceDetailsActivity";

    JSONObject res;
    Intent intent;

    public JSONObject getres(){

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

        mSectionsPageAdapter = new SectionsPageAdapter(getSupportFragmentManager());

        // Set up the ViewPager with the sections adapter.
        mViewPager = (ViewPager) findViewById(R.id.containerPlace);
        setupViewPager(mViewPager);

        TabLayout tabLayout = (TabLayout) findViewById(R.id.tabsPlace);
        tabLayout.setupWithViewPager(mViewPager);
    }

    private void setupViewPager(ViewPager viewPager) {
        SectionsPageAdapter adapter = new SectionsPageAdapter(getSupportFragmentManager());
        adapter.addFragment(new TabInfoFragment(), "Info");
        adapter.addFragment(new TabPhotosFragment(), "Photos");
        adapter.addFragment(new TabMapFragment(), "Map");
        adapter.addFragment(new TabReviewsFragment(), "Reviews");
        viewPager.setAdapter(adapter);
    }

}