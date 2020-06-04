package com.org.locationtracking.fragments;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FieldValue;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.firebase.firestore.SetOptions;
import com.org.locationtracking.activities.AddChildActivity;
import com.org.locationtracking.adapters.ChildListAdapter;
import com.org.locationtracking.adapters.SearchMemberAdapter;
import com.org.locationtracking.activities.SearchParentActivity;
import com.org.locationtracking.LocationApp;
import com.org.locationtracking.databinding.FragmentChildListBinding;
import com.org.locationtracking.models.User;
import com.org.locationtracking.utils.AndroidUtil;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import lombok.val;

/**
 * A simple {@link Fragment} subclass.
 */
public class ChildListFragment
        extends BaseFragment
{

    //this is view binding.this mBinding object contains all view created in xml
    private FragmentChildListBinding mBinding;

    //main rootview for fragment
    private View mRootView;

    //***********************************************************************************************************
    @Override
    public View onCreateViewBaseFragment(LayoutInflater inflater, ViewGroup parent, Bundle savedInstanceState)
    //***********************************************************************************************************
    {
        if (mRootView == null)
        {
            mBinding = FragmentChildListBinding.inflate(inflater, parent, false);
            mRootView = mBinding.getRoot();
            super.setFragment(ChildListFragment.this);
            initControls();
        }
        return mRootView;
    }

    private void initControls()
    {
        //get child list
        getMyChildList();

        //get members list
        getMembersList();

        //get child list shared with me
        getChildListSharedWithMe();

        //add child click listener
        mBinding.addChild.setOnClickListener(view -> gotoAddCHildActivity());

        //add adult click listener
        mBinding.addults.setOnClickListener(view -> gotoAddAdultScreen());

    }

    private void getChildListSharedWithMe()
    {

        //get child click
        val shredChildList = LocationApp.instance()
                                        .getUser()
                                        .getSharedBy();
        if (shredChildList == null || shredChildList.size() == 0)
            return;
        LocationApp.instance()
                   .getMFireStore()
                   .collection("Parent")
                   .whereEqualTo("child", true)
                   .get()
                   .addOnSuccessListener(new OnSuccessListener<QuerySnapshot>()
                   {
                       @Override
                       public void onSuccess(QuerySnapshot queryDocumentSnapshots)
                       {
                           List<User> sharedChildList = new ArrayList<>();
                           //create empty child list
                           for (val child : queryDocumentSnapshots.getDocuments())
                           {
                               val user = child.toObject(User.class);
                               if (shredChildList.indexOf(user.getParentId()) >= 0)
                               {
                                   sharedChildList.add(user);
                                   //add child to child list
                               }
                           }

                           //show child list on recyclerview
                           showSharedChildOnRecyclerView(sharedChildList);

                       }
                   });
    }

    private void showSharedChildOnRecyclerView(List<User> users)
    {
        //show shared child on recyclerview
        ChildListAdapter childLIstAdapter = new ChildListAdapter(users, getActivity(), false, null);
        mBinding.shareChilds.setLayoutManager(new LinearLayoutManager(getContext()));
        mBinding.shareChilds.setAdapter(childLIstAdapter);
    }

    private void getMembersList()
    {
        //get member list from firebase
        val id = LocationApp.instance()
                            .getUser()
                            .getId();

        //get member list bi parent id
        LocationApp.instance()
                   .getMFireStore()
                   .collection("Parent")
                   .whereEqualTo("child", false)
                   .whereArrayContains("sharedBy", id)
                   .addSnapshotListener(new EventListener<QuerySnapshot>()
                   {
                       @Override
                       public void onEvent(@Nullable QuerySnapshot queryDocumentSnapshots, @Nullable FirebaseFirestoreException e)
                       {
                           List<User> membersList = new ArrayList<>();
                           //create empty member list
                           for (val child : queryDocumentSnapshots.getDocuments())
                           {
                               User user = child.toObject(User.class);
                               membersList.add(user);
                               //add member in member list
                           }

                           //show member list on recyclerview
                           showMembersListOnRecyclerView(membersList);
                       }
                   });
    }

    private void showMembersListOnRecyclerView(List<User> membersList)
    {


        //show member list on recyclerview
        SearchMemberAdapter searchMemberAdapter = new SearchMemberAdapter(membersList,
                                                                          new SearchMemberAdapter.MemberClickListener()
                                                                          {
                                                                              @Override
                                                                              public void onMemberClick(User user)
                                                                              {

                                                                              }

                                                                              @Override
                                                                              public void onRemove(User user)
                                                                              {

                                                                                  //remove member when click on remove icon
                                                                                  val prent = LocationApp.instance()
                                                                                                         .getUser();
                                                                                  //Map to remove user from array
                                                                                  final Map<String, Object> addUserFromArrayMap = new HashMap<>();
                                                                                  addUserFromArrayMap.put(
                                                                                          "sharedBy",
                                                                                          FieldValue.arrayRemove(
                                                                                                  prent.getId()));


                                                                                  //remove member from database
                                                                                  LocationApp.instance()
                                                                                             .getMFireStore()
                                                                                             .collection(
                                                                                                     "Parent")
                                                                                             .document(
                                                                                                     user.getId())
                                                                                             .set(addUserFromArrayMap,
                                                                                                  SetOptions.merge())

                                                                                             .addOnCompleteListener(
                                                                                                     new OnCompleteListener<Void>()
                                                                                                     {
                                                                                                         @Override
                                                                                                         public void onComplete(@NonNull Task<Void> task)
                                                                                                         {
                                                                                                             if (task.isSuccessful())
                                                                                                             {
                                                                                                                 AndroidUtil.toast(
                                                                                                                         false,
                                                                                                                         "Medlem fjernet");
                                                                                                             }
                                                                                                         }
                                                                                                     });
                                                                              }
                                                                          }, true);
        mBinding.membersLis.setLayoutManager(new LinearLayoutManager(getContext()));
        mBinding.membersLis.setAdapter(searchMemberAdapter);
    }

    //goto add member screen
    private void gotoAddAdultScreen()
    {
        Intent intent = new Intent(getActivity(), SearchParentActivity.class);
        startActivity(intent);
    }

    //goto child activity
    private void gotoAddCHildActivity()
    {
        Intent childIntent = new Intent(getActivity(), AddChildActivity.class);
        startActivity(childIntent);
    }

    private void getMyChildList()
    {
        //get child list
        LocationApp.instance()
                   .
                           getMFireStore()
                   .collection("Parent")
                   .whereEqualTo("parentId", LocationApp.instance()
                                                        .getUser()
                                                        .getId())
                   .addSnapshotListener(new EventListener<QuerySnapshot>()
                   {
                       @Override
                       public void onEvent(@Nullable QuerySnapshot queryDocumentSnapshots, @Nullable FirebaseFirestoreException e)
                       {
                           List<User> userList = new ArrayList<>();
                           //create empty child list
                           for (val child : queryDocumentSnapshots.getDocuments())
                           {
                               val user = child.toObject(User.class);
                               userList.add(user);
                               //add user to child list
                           }
                           //show child list on recyclerview
                           showMyChildOnRecyclerView(userList);
                       }
                   });
    }

    private void showMyChildOnRecyclerView(List<User> userList)
    {
        //show child list on recyclerview
        ChildListAdapter childLIstAdapter =
                new ChildListAdapter(userList, getActivity(), true,
                                     user -> LocationApp.instance()//remove child from db
                                                        .getMFireStore()
                                                        .collection("Parent")
                                                        .document(user.getId())
                                                        .delete()
                                                        .addOnCompleteListener(
                                                                task -> {
                                                                }));
        mBinding.recyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
        mBinding.recyclerView.setAdapter(childLIstAdapter);
    }
}
