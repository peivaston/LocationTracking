package com.org.locationtracking.activities;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.databinding.DataBindingUtil;
import lombok.val;

import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.org.locationtracking.R;
import com.org.locationtracking.LocationApp;
import com.org.locationtracking.databinding.ActivityForgotPaswordBinding;
import com.org.locationtracking.utils.AndroidUtil;
import com.org.locationtracking.utils.UIUtils;

//******************************************************
public class ForgotPasswordActivity
        extends AppCompatActivity
        //******************************************************
{

    //this is view binding.this mBinding object contains all view created in xml
    private ActivityForgotPaswordBinding mBinding;

    //onCreate method that runs when activity started
    //******************************************************
    @Override
    protected void onCreate(Bundle savedInstanceState)
    //******************************************************
    {
        super.onCreate(savedInstanceState);

        //initialize binding with xml layout
        mBinding = DataBindingUtil.setContentView(this, R.layout.activity_forgot_pasword);

        //function will run first time when activity started.all activity contains initControls function
        initControls();
    }

    //******************************************************
    private void initControls()
    //******************************************************
    {
        //mBinding.back is back button image view when click its goto back to previous screen
        mBinding.back.setOnClickListener(view -> finish());

        //mBinding.sendEmail .  it will call sendEmail function when click on sendEmail
        mBinding.sendEmail.setOnClickListener(view -> sendEmail());
    }

    //******************************************************
    private void sendEmail()
    //******************************************************
    {
        //getEmail from editText email and save as string in email
        val email = mBinding.emailEdit.getText()
                                      .toString();


        //check whether email is empty or not if empty than condition  will true
        if (TextUtils.isEmpty(email))
        {
            //if email is empty than set error to edittext
            mBinding.emailEdit.setError(getResources().getString(R.string.required));

            //focus keyboard on edittext name
            mBinding.emailEdit.requestFocus();
            //return from function
            return;
        }


        //check whether email is valid or not
        if (!UIUtils.isValidEmailId(email))
        {
            //if email is not valid than shoe error
            mBinding.emailEdit.setError(getResources().getString(R.string.email_is_invalid));
            mBinding.emailEdit.requestFocus();
            //return from function
            return;
        }

        //show progress view
        mBinding.progressView.setVisibility(View.VISIBLE);

        //call Firebase auth instance send verification email
        LocationApp.instance()
                   .getMAuth()
                   .sendPasswordResetEmail(email)
                   .addOnCompleteListener(
                           new OnCompleteListener<Void>()
                           {
                               @Override
                               public void onComplete(@NonNull Task<Void> task)
                               {
                                   //hide progress view
                                   mBinding.progressView.setVisibility(View.GONE);
                                   if (!task.isSuccessful())
                                   {
                                       //show error if task in unsucessfull
                                       AndroidUtil.toast(true,
                                                         "child cannot change password.or user not exist");
                                       return;
                                   }
                                   //hide progress view
                                   mBinding.progressView.setVisibility(View.GONE);

                                   //show success messgae if email sent
                                   AndroidUtil.toast(true, getResources().getString(
                                           R.string.password_reset_email_Sent));

                                   //destroy the screen
                                   finish();
                               }
                           });
    }
}
