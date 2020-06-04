package com.org.locationtracking.activities;

import android.content.Intent;
import android.os.Bundle;

import com.google.android.material.tabs.TabLayout;
import com.google.firebase.firestore.SetOptions;
import com.google.firebase.iid.FirebaseInstanceId;
import com.org.locationtracking.LocationApp;
import com.org.locationtracking.R;
import com.org.locationtracking.adapters.CustomFragmentPageAdapter;
import com.org.locationtracking.databinding.ActivityParentBinding;
import com.org.locationtracking.fragments.ChildListFragment;

import java.util.HashMap;

import androidx.appcompat.app.AppCompatActivity;
import androidx.databinding.DataBindingUtil;
import lombok.val;

//******************************************************
public class ParentActivity
        extends AppCompatActivity
        //******************************************************
{

    //this is view binding.this mBinding object contains all view created in xml
    private ActivityParentBinding mBinding;


    //******************************************************
    @Override
    protected void onCreate(Bundle savedInstanceState)
    //******************************************************
    {
        super.onCreate(savedInstanceState);
        //initialize binding with xml layout
        mBinding = DataBindingUtil.setContentView(this, R.layout.activity_parent);

        //call function that contains main logic
        initControls();
    }

    //******************************************************
    private void initControls()
    //******************************************************
    {
        //view pager for swipre layout
        attachViewPageAdaptor();

        //click listener  logout
        mBinding.logout.setOnClickListener(view -> gotoLoginActivity());

        //update fcm token to database
        updateFCMToken();
    }

    private void updateFCMToken()
    {
        //get firebase fcm toek
        FirebaseInstanceId.getInstance()
                          .getInstanceId()
                          .addOnSuccessListener(
                                  instanceIdResult -> {

                                      //update fcm token to database
                                      val documentId = LocationApp.instance()
                                                                  .getUser()
                                                                  .getId();
                                      HashMap<String, Object> fcm = new HashMap<>();
                                      fcm.put("fcmToken", instanceIdResult.getToken());
                                      LocationApp.instance()
                                                 .getMFireStore()
                                                 .collection("Parent")
                                                 .document(documentId)
                                                 .set(fcm, SetOptions.merge())
                                                 .addOnCompleteListener(task -> {

                                                 });
                                  });
    }

    //goto login activity
    //******************************************************
    private void gotoLoginActivity()
    //******************************************************
    {
        Intent loginIntent = new Intent(this, MainActivity.class);
        startActivity(loginIntent);
        finish();
    }


    //attach viewpager
    //********************************************************
    private void attachViewPageAdaptor()
    //********************************************************
    {
        CustomFragmentPageAdapter mCustomFragmentPageAdapter = new CustomFragmentPageAdapter(
                getSupportFragmentManager());
        mCustomFragmentPageAdapter.addFragment(new ChildListFragment());
        mBinding.viewPager.setOffscreenPageLimit(0);
        mBinding.viewPager.setAdapter(mCustomFragmentPageAdapter);
        mBinding.viewPager.setCurrentItem(0);
        mBinding.viewPager.setOffscreenPageLimit(4);
        mBinding.viewPager.addOnPageChangeListener(
                new TabLayout.TabLayoutOnPageChangeListener(mBinding.tabLayout));
        mBinding.tabLayout.setOnTabSelectedListener(new TabLayout.OnTabSelectedListener()
        {
            @Override
            public void onTabSelected(TabLayout.Tab tab)
            {
                //set current tab
                mBinding.viewPager.setCurrentItem(tab.getPosition());
            }

            @Override
            public void onTabUnselected(TabLayout.Tab tab)
            {
            }

            @Override
            public void onTabReselected(TabLayout.Tab tab)
            {
            }
        });
    }

}
