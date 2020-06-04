package com.org.locationtracking.activities;

import android.os.Bundle;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.FieldValue;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.firebase.firestore.SetOptions;
import com.org.locationtracking.LocationApp;
import com.org.locationtracking.R;
import com.org.locationtracking.adapters.SearchMemberAdapter;
import com.org.locationtracking.databinding.ActivitySearchParentBinding;
import com.org.locationtracking.models.User;
import com.org.locationtracking.utils.AndroidUtil;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.databinding.DataBindingUtil;
import androidx.recyclerview.widget.LinearLayoutManager;
import lombok.val;

//******************************************************
public class SearchParentActivity
        extends AppCompatActivity
        implements SearchMemberAdapter.MemberClickListener
//******************************************************
{

    //this is view binding.this mBinding object contains all view created in xml
    private ActivitySearchParentBinding mBinding;

    //contains list for member filter by search
    private List<User> mFilteredList;

    //contain list of aal member
    private List<User> mParentList;

    //search member adapter
    private SearchMemberAdapter mSearchMemberAdapter;

    //******************************************************
    @Override
    protected void onCreate(Bundle savedInstanceState)
    //******************************************************
    {
        super.onCreate(savedInstanceState);

        //initialize binding with xml layout
        mBinding = DataBindingUtil.setContentView(this, R.layout.activity_search_parent);

        //call function that contains main logic
        initControls();
    }

    //******************************************************
    private void initControls()
    //******************************************************
    {

        //mBinding.back is back button image view when click its goto back to previous screen
        mBinding.back.setOnClickListener(view -> finish());

        //initialize filtered list
        mFilteredList = new ArrayList<>();

        //init recyclerview
        initRecyclerView();

        //get members list
        getParentList();

        //add text change listener for member search
        addTextChangeListener();
    }


    //add text change listener for member search
    private void addTextChangeListener()
    {
        mBinding.searchParent.addTextChangedListener(new TextWatcher()
        {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after)
            {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count)
            {
                if (s.length() == 0)
                {
                    //if search text is empty show all members list
                    mFilteredList.clear();
                    mFilteredList.addAll(mParentList);
                    mSearchMemberAdapter.notifyDataSetChanged();
                }
                else
                    //filter member by name and email
                    filterParent(s.toString());

            }

            @Override
            public void afterTextChanged(Editable s)
            {

            }
        });
    }


    //show member search by parent
    private void filterParent(String toString)
    {
        mFilteredList.clear();

        for (val member : mParentList)
        {
            //if search text found in name or email han add member in filter list
            if (member.getEmail()
                      .contains(toString) || (!TextUtils.isEmpty(
                    member.getUserName())) && member.getUserName()
                                                    .toLowerCase()
                                                    .contains(toString))
            {
                mFilteredList.add(member);
            }
        }
        //show member on recyclerview
        mSearchMemberAdapter.notifyDataSetChanged();
    }

    private void initRecyclerView()
    {

        //init recyclerview
        mSearchMemberAdapter = new SearchMemberAdapter(mFilteredList, this, false);
        mBinding.recyclerView.setLayoutManager(new LinearLayoutManager(getApplicationContext()));
        mBinding.recyclerView.setAdapter(mSearchMemberAdapter);

    }

    private void getParentList()
    {
        //get all parent list using firestore instance
        LocationApp.instance()
                   .getMFireStore()
                   .collection("Parent")
                   .whereEqualTo("child", false)
                   .get()
                   .addOnSuccessListener(new OnSuccessListener<QuerySnapshot>()
                   {
                       @Override
                       public void onSuccess(QuerySnapshot queryDocumentSnapshots)
                       {
                           mParentList = new ArrayList<>();
                           //add member list in list
                           for (val child : queryDocumentSnapshots.getDocuments())
                           {
                               val user = child.toObject(User.class);
                               mParentList.add(user);
                           }
                           //show member list on recyclerview
                           mFilteredList.addAll(mParentList);
                           mSearchMemberAdapter.notifyDataSetChanged();
                       }
                   });
    }


    //call when parent click on member
    @Override
    public void onMemberClick(User user)
    {
        val prent = LocationApp.instance()
                               .getUser();
        //Map to remove user from array

        final Map<String, Object> addUserFromArrayMap = new HashMap<>();
        addUserFromArrayMap.put("sharedBy", FieldValue.arrayUnion(prent.getId()));


        //add parent id to member detail
        LocationApp.instance()
                   .getMFireStore()
                   .collection("Parent")
                   .document(user.getId())
                   .set(addUserFromArrayMap, SetOptions.merge())

                   .addOnCompleteListener(new OnCompleteListener<Void>()
                   {
                       @Override
                       public void onComplete(@NonNull Task<Void> task)
                       {
                           if (task.isSuccessful())
                           {
                               //member added successfully than show msg
                               AndroidUtil.toast(true,
                                                 "Nå har " + user.getUserName() + " tilgang til ditt barns lokasjon." );
                               finish();
                           }
                       }
                   });

    }

    @Override
    public void onRemove(User user)
    {

    }
}
