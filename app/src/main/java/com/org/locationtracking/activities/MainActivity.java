package com.org.locationtracking.activities;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.firestore.QuerySnapshot;
import com.org.locationtracking.R;
import com.org.locationtracking.LocationApp;
import com.org.locationtracking.databinding.ActivityMainBinding;
import com.org.locationtracking.models.Stations;
import com.org.locationtracking.models.User;
import com.org.locationtracking.utils.AndroidUtil;
import com.org.locationtracking.utils.UIUtils;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.List;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.databinding.DataBindingUtil;
import lombok.val;

//******************************************************
public class MainActivity
        extends AppCompatActivity
        //******************************************************
{

    //this is view binding.this mBinding object contains all view created in xml
    private ActivityMainBinding mBinding;


    //onCreate method that runs when activity started
    //******************************************************
    @Override
    protected void onCreate(Bundle savedInstanceState)
    //******************************************************
    {
        super.onCreate(savedInstanceState);
        //initialize binding with xml layout
        mBinding = DataBindingUtil.setContentView(this, R.layout.activity_main);

        //function will run first time when activity started.all activity contains initControls function
        initControl();
    }

    //******************************************************
    private void initControl()
    //******************************************************
    {
        //login click listener
        mBinding.loginButton.setOnClickListener(view -> submit());

        //signup click listener
        mBinding.signup.setOnClickListener(view -> gotoSignUpActivity());

        //forgot password click listener
        mBinding.forgotPassword.setOnClickListener(view -> forgotPassword());
        saveData();
    }

    //******************************************************
    private void saveData()
    //******************************************************
    {

        //read scv as input
        InputStream in = getResources()
                .openRawResource(R.raw.stattion_list);
        BufferedReader reader = new BufferedReader(
                new InputStreamReader(in, Charset.forName("UTF-8")));

        String line = "";
        try
        {
            //create empty station list
            List<Stations> stations = new ArrayList<>();

            //read csv line by line
            while ((line = reader.readLine()) != null)
            {
                //split line by ,
                val stationOBJ = line.split(",");

                //set station detail to station oject
                val station = new Stations(stationOBJ[0], stationOBJ[1], stationOBJ[2],
                                           System.currentTimeMillis());

                //add station to station list
                stations.add(station);
            }

            //set station list to list placed in location application class
            LocationApp.instance()
                       .setStationList(stations);
        }
        catch (Exception e)
        {
            //AndroidUtil.toast(false, e.getLocalizedMessage());
        }
    }

    //goto password screen
    //******************************************************
    private void forgotPassword()
    //******************************************************
    {
        Intent forgotIntent = new Intent(this, ForgotPasswordActivity.class);
        startActivity(forgotIntent);
    }


    //goto signup screen
    //******************************************************
    private void gotoSignUpActivity()
    //******************************************************
    {
        Intent signUpIntent = new Intent(this, SignUpActivity.class);
        startActivity(signUpIntent);
    }

    //******************************************************
    private void submit()
    //******************************************************
    {

        //getEmail from editText email and save as string in email
        val email = mBinding.emailEdit.getText()
                                      .toString();

        //getPassword from editText password and save as string in password
        val password = mBinding.passwordEdit.getText()
                                            .toString();

        //check whether email is empty or not if empty than condition  will true
        if (TextUtils.isEmpty(email))
        {
            //if email is empty than set error to edittext
            mBinding.emailEdit.setError(getResources().getString(R.string.required));
            //focus keyboard on edittext email
            mBinding.emailEdit.requestFocus();
            //return from function
            return;
        }

        //check whether email is valid or not
        if (!UIUtils.isValidEmailId(email))
        {
            //if email is not valid than shoe error
            mBinding.emailEdit.setError(getResources().getString(R.string.email_is_invalid));
            //return from function
            return;
        }

        //check whether password is empty or not if empty than condition  will true
        if (TextUtils.isEmpty(password))
        {
            //if password is empty than show password empty error
            mBinding.passwordEdit.setError(getResources().getString(R.string.required));
            //focus keyboard to password input
            mBinding.passwordEdit.requestFocus();
            //return from function
            return;
        }

        //if password length is less than 6 than condition will
        if (password.length() < 6)
        {
            //show error
            mBinding.passwordEdit.setError("Passordet må bestå av minst 6 tegn");
            //return from function
            return;
        }

        //show progress view
        mBinding.progressView.setVisibility(View.VISIBLE);
        loginUser(email, password);
    }

    //******************************************************
    private void loginUser(String email, String password)
    //******************************************************
    {
        //create firestore instance for login user
        LocationApp.instance()
                   .getMFireStore()
                   .collection("Parent")
                   .whereEqualTo("email", email)//get user where email is equal to
                   .whereEqualTo("password", password)//get user where password is equal to
                   .get()
                   .addOnSuccessListener(new OnSuccessListener<QuerySnapshot>()
                   {
                       @Override
                       public void onSuccess(QuerySnapshot queryDocumentSnapshots)
                       {
                           //hide progress view
                           mBinding.progressView.setVisibility(View.GONE);

                           //if account not found than show error user not exist
                           if (queryDocumentSnapshots.isEmpty())
                           {
                               AndroidUtil.toast(false, "Feil brukernavn eller passord");
                               return;
                           }

                           // if user found than create new user
                           User user = new User();

                           //loop through each user
                           for (val child : queryDocumentSnapshots.getDocuments())
                           {
                               //set user detail to user object
                               user = child.toObject(User.class);
                           }

                           //set user detail to application class
                           LocationApp.instance()
                                      .setUser(user);

                           //if current user is child goto child activity
                           if (user.isChild())
                               startActivity(new Intent(MainActivity.this, ChildActivity.class));
                           else
                               //goto parent screen
                               startActivity(new Intent(MainActivity.this, ParentActivity.class));
                           finish();

                       }
                   })
                   .addOnFailureListener(new OnFailureListener()
                   {
                       @Override
                       public void onFailure(@NonNull Exception e)
                       {
                           //if error accurred show error in toast
                           AndroidUtil.toast(false, e.getLocalizedMessage()
                                                     .toString());

                       }
                   });

    }

}
