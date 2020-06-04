package com.org.locationtracking.fragments;

import android.app.Activity;
import android.app.Dialog;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;


import com.org.locationtracking.R;
import com.org.locationtracking.utils.AndroidUtil;

import androidx.fragment.app.Fragment;
import lombok.NonNull;


//************************************************************
public abstract class BaseFragment
        extends Fragment
//************************************************************
{

    private Activity mBaseActivity;
    private Fragment mFragment;
    private Dialog mLoadingBar;
    protected View mRootView;

    //************************************************************
    public void onCreate(Bundle savedInstanceState)
    //************************************************************
    {
        super.onCreate(savedInstanceState);
    }

    //************************************************************************************************
    public View onCreateView(LayoutInflater inflater, ViewGroup parent, Bundle savedInstanseState)
    //************************************************************************************************
    {
        View view = onCreateViewBaseFragment(inflater, parent, savedInstanseState);
        return view;
    }

    //*********************************************************************
    @NonNull
    public abstract View onCreateViewBaseFragment(LayoutInflater inflater, ViewGroup parent, Bundle savedInstanceState);
    //*********************************************************************

    //****************************************************************
    public void setFragment(Fragment fragment)
    //****************************************************************
    {
        if (mFragment != null)
            return;
        mFragment = fragment;
        mBaseActivity = mFragment.getActivity();
    }





    //***********************************************************************
    public void showLoadingDialog()
    //***********************************************************************
    {
        if (mBaseActivity == null)
            return;
        if (mLoadingBar != null)
        {
            mLoadingBar.show();
            return;
        }
        mLoadingBar = new Dialog(mBaseActivity, R.style.CustomTransparentDialog);
        mLoadingBar.setContentView(R.layout.dialog_loading_bar);
        mLoadingBar.setCancelable(false);
        mLoadingBar.setCanceledOnTouchOutside(false);
        mLoadingBar.show();
    }

    //***********************************************************************
    public void hideLoadingDialog()
    //***********************************************************************
    {
        if (mLoadingBar != null)
        {
            mLoadingBar.dismiss();
        }
    }

}
