package com.org.locationtracking;

import android.app.Application;

import com.google.firebase.FirebaseApp;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreSettings;
import com.google.firebase.storage.FirebaseStorage;
import com.org.locationtracking.models.Stations;
import com.org.locationtracking.models.User;
import com.org.locationtracking.utils.AndroidUtil;

import java.util.List;

import lombok.Getter;
import lombok.NonNull;
import lombok.Setter;
import lombok.val;


//**************************************************************************
public class LocationApp
        extends Application
//**************************************************************************
{
    //firebase authentication
    @Getter private FirebaseAuth mAuth;

    //firebase firestore for storing data
    @Getter private FirebaseFirestore mFireStore;

    //use to store user detail
    @Getter @Setter User user;

    //use to store station list
    @Getter @Setter List<Stations> stationList;


    //**************************************************************************
    @Override
    public void onCreate()
    //**************************************************************************
    {
        super.onCreate();
        //initializing firebase
        AndroidUtil.setContext(this);
        val app = FirebaseApp.initializeApp(this);
        mAuth = FirebaseAuth.getInstance();
        mFireStore = FirebaseFirestore.getInstance();
        FirebaseFirestoreSettings settings = new FirebaseFirestoreSettings.Builder().build();
        mFireStore.setFirestoreSettings(settings);
    }

    public void isEmailExist(String email, UserDetailListener listener)
    {

        //check email exist or not
        mFireStore.collection("Parent")
                  .whereEqualTo("email", email)
                  .get()
                  .addOnSuccessListener(
                          queryDocumentSnapshots -> {
                              if (!queryDocumentSnapshots.isEmpty())
                              {
                                  if (listener != null)
                                      listener.onUserLoaded(true);
                              }
                              else
                              {
                                  listener.onUserLoaded(false);
                              }
                          });
    }

    //*********************************************************************
    public static @NonNull
    LocationApp instance()
    //*********************************************************************
    {
        return (LocationApp)AndroidUtil.getApplicationContext();

    }

    public interface UserDetailListener
    {
        void onUserLoaded(boolean isEmailExist);
    }

}
