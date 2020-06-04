package com.org.locationtracking.activities;

import androidx.appcompat.app.AppCompatActivity;
import androidx.databinding.DataBindingUtil;

import android.content.Intent;
import android.os.Bundle;

import com.org.locationtracking.R;
import com.org.locationtracking.databinding.ActivityIntro1Binding;
import com.org.locationtracking.databinding.ActivityIntro4Binding;

public class Intro4Activity
        extends AppCompatActivity
{

    private ActivityIntro4Binding mBinding;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        mBinding = DataBindingUtil.setContentView(this, R.layout.activity_intro4);
        initControls();
    }

    private void initControls()
    {
        mBinding.nesteBtn.setOnClickListener(view -> {
            startActivity(new Intent(this, Intro5Activity.class));
            finish();

        });

        mBinding.cardView.setOnClickListener(view -> {
            startActivity(new Intent(this, Intro1Activity.class));
            finish();
        });

        mBinding.cardView2.setOnClickListener(view -> {
            startActivity(new Intent(this, Intro3Activity.class));
            finish();
        });

        mBinding.cardView3.setOnClickListener(view -> {
            startActivity(new Intent(this, Intro2Activity.class));
            finish();
        });

        mBinding.cardView4.setOnClickListener(view -> {
            startActivity(new Intent(this, Intro4Activity.class));
            finish();
        });

        mBinding.cardView5.setOnClickListener(view -> {
            startActivity(new Intent(this, Intro5Activity.class));
            finish();
        });
    }
}