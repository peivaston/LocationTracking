package com.org.locationtracking.activities;

import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.Task;
import com.org.locationtracking.LocationApp;
import com.org.locationtracking.R;
import com.org.locationtracking.databinding.ActivityAddChildBinding;
import com.org.locationtracking.models.User;
import com.org.locationtracking.utils.AndroidUtil;
import com.org.locationtracking.utils.UIUtils;

import java.util.UUID;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.databinding.DataBindingUtil;
import lombok.val;

public class AddChildActivity
        extends AppCompatActivity
{

    //this is view binding.this mBinding object contains all view created in xml
    private ActivityAddChildBinding mBinding;

    //onCreate method that runs when activity started

    @Override
    protected void onCreate(Bundle savedInstanceState)

    {
        super.onCreate(savedInstanceState);
        //initialize binding with xml layout
        mBinding = DataBindingUtil.setContentView(this, R.layout.activity_add_child);

        //function will run first time when activity started.all activity contains initControls function
        initControls();
    }

    private void initControls()

    {
        //mBinding.back is back button image view when click its goto back to previous screen
        mBinding.back.setOnClickListener(view -> finish());

        //mBinding.signup signup button it will call submit function when click on signup
        mBinding.signup.setOnClickListener(view -> submit());
    }

    private void submit()

    {
        //getName from editText name and save as string in name
        val name = mBinding.name.getText().toString();

        //getEmail from editText email and save as string in email
        val email = mBinding.emailEdit.getText().toString();

        //getPassword from editText password and save as string in password
        val password = mBinding.passwordEdit.getText().toString();

        //check whether name is empty or not if empty then condition  will true
        if (TextUtils.isEmpty(name))
        {
            //if name is empty then set error to edittext
            mBinding.name.setError(getResources().getString(R.string.required));
            //focus keyboard on edittext name
            mBinding.name.requestFocus();
            //return from function
            return;
        }

        //check whether email is empty or not if empty then condition  will true
        if (TextUtils.isEmpty(email))
        {
            //if email is empty then set error to edittext
            mBinding.emailEdit.setError(getResources().getString(R.string.required));
            //focus keyboard on edittext email
            mBinding.emailEdit.requestFocus();
            //return from function
            return;
        }

        //check whether email is valid or not
        if (!UIUtils.isValidEmailId(email))
        {
            //if email is not valid then show error
            mBinding.emailEdit.setError(getResources().getString(R.string.email_is_invalid));
            //return from function
            return;
        }

        //check whether password is empty or not if empty then condition  will true
        if (TextUtils.isEmpty(password))
        {
            //if password is empty then show password empty error
            mBinding.passwordEdit.setError(getResources().getString(R.string.required));
            //focus keyboard to password input
            mBinding.passwordEdit.requestFocus();
            //return from function
            return;
        }

        //if password length is less than 6 then condition will
        if (password.length() < 6)
        {
            //show error
            mBinding.passwordEdit.setError("Passordet må bestå av minst 6 tegn");
            //return from function
            return;
        }

        //show progress view
        mBinding.progressView.setVisibility(View.VISIBLE);

        //call application class check if email exists or not
        LocationApp.instance()
                   .isEmailExist(email, new LocationApp.UserDetailListener()
                   {
                       @Override
                       public void onUserLoaded(boolean isEmailExist)
                       {
                           //hide progress view
                           mBinding.progressView.setVisibility(View.GONE);

                           //if email does not exist then add child in database
                           if (!isEmailExist)
                               //call signup function
                               signUpUser(name, email, password);

                               //if email exist then show email exist error
                           else
                               //androidUtil class is helper class that contains utility function for showing toast
                               AndroidUtil.toast(true, "Eposten eksisterer");

                       }
                   });
    }

    private void signUpUser(String name, String email, String password)

    {
        //show progress dialog
        mBinding.progressView.setVisibility(View.VISIBLE);

        //create empty user
        val user = new User();

        //create random user id
        user.setId(UUID.randomUUID()
                       .toString());

        //set username enter by parent
        user.setUserName(name);

        //set email enter by parent
        user.setEmail(email);

        //set child true because we are adding child
        user.setChild(true);

        //set child password added by parent
        user.setPassword(password);

        //set parent id which add child
        user.setParentId(LocationApp.instance()
                                    .getUser()
                                    .getId());

        //create instance of firestore then open collection with Parent after
        //that save child detail in set method
        LocationApp.instance()
                   .getMFireStore()
                   .collection("Parent")
                   .document(user.getId())
                   .set(user)
                   .addOnCompleteListener(new OnCompleteListener<Void>()
                   {
                       @Override
                       public void onComplete(@NonNull Task<Void> task)
                       {
                           // hide progress view
                           mBinding.progressView.setVisibility(
                                   View.GONE);

                           //if data succeessfully added then show toast and return
                           if (task.isSuccessful())
                           {
                               AndroidUtil.toast(false,
                                                 "Barn lagt til");
                               finish();
                               return;
                           }

                           //if error in firestore then show exception in toast
                           AndroidUtil.toast(true, task.getException()
                                                        .getLocalizedMessage());

                       }
                   })
                   .addOnFailureListener(new OnFailureListener()
                   {
                       @Override
                       public void onFailure(@NonNull Exception e)
                       {
                           //if something went wrong then show exception
                           AndroidUtil.toast(true,
                                             e.getLocalizedMessage()
                                              .toString());

                       }
                   });
    }
}
