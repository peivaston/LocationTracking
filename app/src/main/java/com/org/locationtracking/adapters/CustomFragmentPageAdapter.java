package com.org.locationtracking.adapters;


import java.util.ArrayList;
import java.util.List;

import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentStatePagerAdapter;
import lombok.NonNull;

//*****************************************************
public class CustomFragmentPageAdapter
        extends FragmentStatePagerAdapter
//*****************************************************
{


    //fragment list adapter
    private List<Fragment> mFragmentList;

    //*****************************************************
    public CustomFragmentPageAdapter(FragmentManager fm)
    //*****************************************************
    {
        super(fm);
        mFragmentList = new ArrayList<>();
    }

    public void addFragment(@NonNull Fragment fragment)
    {
        mFragmentList.add(fragment);

    }

    //*****************************************************
    @Override
    public Fragment getItem(int fragmentId)
    //*****************************************************
    {
        return mFragmentList.get(fragmentId);
    }


    //*****************************************************
    @Override
    public int getCount()
    //*****************************************************
    {
        return mFragmentList.size();
    }
}
