package com.org.locationtracking.activities;

import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.org.locationtracking.R;
import com.org.locationtracking.LocationApp;
import com.org.locationtracking.databinding.ActivitySignUpBinding;
import com.org.locationtracking.models.User;
import com.org.locationtracking.utils.AndroidUtil;
import com.org.locationtracking.utils.UIUtils;

import java.util.UUID;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.databinding.DataBindingUtil;
import lombok.val;

public class SignUpActivity
        extends AppCompatActivity
{

    //this is view binding.this mBinding object contains all view created in xml
    private ActivitySignUpBinding mBinding;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);

        //initialize binding with xml layout
        mBinding = DataBindingUtil.setContentView(this, R.layout.activity_sign_up);

        //call function that contains main logic
        initControls();
    }


    private void initControls()
    {
        //mBinding.back is back button image view when click its goto back to previous screen
        mBinding.back.setOnClickListener(view -> finish());

        //signup click listener
        mBinding.signup.setOnClickListener(view -> submit());

    }

    private void submit()
    {
        //getName from editText name and save as string in name
        val name = mBinding.name.getText()
                                .toString();

        //getEmail from editText email and save as string in email
        val email = mBinding.emailEdit.getText()
                                      .toString();

        //getPassword from editText password and save as string in password
        val password = mBinding.passwordEdit.getText()
                                            .toString();

        //check whether name is empty or not if empty than condition  will true
        if (TextUtils.isEmpty(name))
        {
            //if name is empty than set error to edittext
            mBinding.name.setError(getResources().getString(R.string.required));
            //focus keyboard on edittext name
            mBinding.name.requestFocus();
            //return from function
            return;
        }

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
            mBinding.passwordEdit.setError("Passord må bestå av minst 6 tegn");
            //return from function
            return;
        }

        //show progress view
        mBinding.progressView.setVisibility(View.VISIBLE);


        //check email exist or not
        LocationApp.instance()
                   .isEmailExist(email, new LocationApp.UserDetailListener()
                   {
                       @Override
                       public void onUserLoaded(boolean isEmailExist)
                       {
                           //hide progress view
                           mBinding.progressView.setVisibility(View.GONE);

                           //if email not exist signup as user
                           if (!isEmailExist)
                               signUpUser(name, email, password);
                           else
                               //if email exist than show error
                               AndroidUtil.toast(true, "Eposten eksisterer");

                       }
                   });
    }

    private void signUpUser(String name, String email, String password)
    {

        //show progress view
        mBinding.progressView.setVisibility(View.VISIBLE);

        //signup user using firebase auth
        LocationApp.instance()
                   .getMAuth()
                   .createUserWithEmailAndPassword(email, password)
                   .addOnCompleteListener(new OnCompleteListener<AuthResult>()
                   {
                       @Override
                       public void onComplete(@NonNull Task<AuthResult> task)
                       {
                           if (task.isSuccessful())
                           {
                               //create user
                               val user = new User();

                               //set random id to user
                               user.setId(UUID.randomUUID()
                                              .toString());

                               //set username
                               user.setUserName(name);

                               //set email
                               user.setEmail(email);

                               //set password
                               user.setPassword(password);


                               //call firesore instance and save user detail to firestore
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
                                                  //hide progress view
                                                  mBinding.progressView.setVisibility(
                                                          View.GONE);


                                                  //show success dialog if accpunt created
                                                  if (task.isSuccessful())
                                                  {
                                                      AndroidUtil.toast(true,
                                                                        "Bruker opprettet");
                                                      finish();
                                                      return;
                                                  }

                                              }
                                          })
                                          .addOnFailureListener(new OnFailureListener()
                                          {
                                              @Override
                                              public void onFailure(@NonNull Exception e)
                                              {
                                                  //show error message if account not created
                                                  AndroidUtil.toast(true,
                                                                    e.getLocalizedMessage()
                                                                     .toString());

                                              }
                                          });
                           }
                           else
                           {
                               //show exception if signup not working
                               AndroidUtil.toast(true, task.getException()
                                                            .getLocalizedMessage());
                               mBinding.progressBar.setVisibility(View.GONE);
                           }
                       }
                   });

    }
}
