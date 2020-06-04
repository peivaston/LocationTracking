package com.org.locationtracking.adapters;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.org.locationtracking.R;
import com.org.locationtracking.activities.MapActivity;
import com.org.locationtracking.databinding.ListViewChildListBinding;
import com.org.locationtracking.models.User;

import java.util.List;

import androidx.databinding.DataBindingUtil;
import androidx.recyclerview.widget.RecyclerView;
import lombok.NonNull;
import lombok.val;

public class ChildListAdapter
        extends RecyclerView.Adapter<ChildListAdapter.ViewHolder>
{
    //user list
    private List<User> mUserList;

    //activity
    private Activity mActivity;
    private boolean mShowPassword;
    private ListClickListener mListener;

    public ChildListAdapter(List<User> mUserList, Activity mActivity, boolean mShowPassword, ListClickListener mListener)
    {
        this.mUserList = mUserList;
        this.mActivity = mActivity;
        this.mShowPassword = mShowPassword;
        this.mListener = mListener;
    }

    //**********************************************
    @Override
    public int getItemViewType(int position)
    //**********************************************
    {
        return position;
    }

    //**********************************************
    @Override
    public long getItemId(int position)
    {
        return super.getItemId(position);
    }
    //**********************************************

    //**********************************************
    @androidx.annotation.NonNull
    @Override
    public ViewHolder onCreateViewHolder(@androidx.annotation.NonNull ViewGroup parent, int viewType)
    //**********************************************
    {
        ListViewChildListBinding mBinding = DataBindingUtil.inflate(
                LayoutInflater.from(parent.getContext()), R.layout.list_view_child_list,
                parent,
                false);
        ViewHolder holder = new ViewHolder(mBinding);
        return holder;
    }


    //**********************************************
    @SuppressLint("SetTextI18n")
    @Override
    public void onBindViewHolder(@androidx.annotation.NonNull ViewHolder holder, int position)
    //**********************************************
    {
        val item = mUserList.get(position);

        //set user detail

        holder.mBinding.name.setText(item.getUserName());
        holder.mBinding.email.setText(item.getEmail());
        if (mShowPassword)
            holder.mBinding.password.setText(item.getPassword());
        else
            holder.mBinding.password.setVisibility(View.GONE);
        holder.mBinding.mainView.setOnClickListener(view -> {
            Intent intent = new Intent(mActivity, MapActivity.class);
            intent.putExtra(MapActivity.USER_ID, item.getId());
            mActivity.startActivity(intent);
        });
        if (mListener == null)
            holder.mBinding.remove.setVisibility(View.GONE);
        else
        {
            holder.mBinding.remove.setVisibility(View.VISIBLE);
            holder.mBinding.remove.setOnClickListener(view -> {
                if (mListener != null)
                    mListener.onRemoveCLick(item);
            });
        }
    }

    //**********************************************
    @Override
    public int getItemCount()
    //**********************************************
    {
        return mUserList.size();
    }

    //**********************************************
    public class ViewHolder
            extends RecyclerView.ViewHolder
            //**********************************************
    {
        ListViewChildListBinding mBinding;

        //**********************************************
        public ViewHolder(@NonNull ListViewChildListBinding itemView)
        //**********************************************
        {
            super(itemView.getRoot());
            mBinding = itemView;
        }
    }


    public interface ListClickListener
    {
        void onRemoveCLick(User user);
    }
}
