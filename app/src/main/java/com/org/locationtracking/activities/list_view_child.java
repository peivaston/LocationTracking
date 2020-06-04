package com.org.locationtracking.activities;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;

import com.org.locationtracking.R;

public class list_view_child
        extends AppCompatActivity
{

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.list_view_child_list);
    }
}
