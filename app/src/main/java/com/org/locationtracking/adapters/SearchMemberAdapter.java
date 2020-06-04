package com.org.locationtracking.adapters;

import android.annotation.SuppressLint;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.org.locationtracking.R;
import com.org.locationtracking.databinding.ListViewMembersBinding;
import com.org.locationtracking.models.User;

import java.util.List;

import androidx.databinding.DataBindingUtil;
import androidx.recyclerview.widget.RecyclerView;
import lombok.NonNull;
import lombok.val;

public class SearchMemberAdapter
        extends RecyclerView.Adapter<SearchMemberAdapter.ViewHolder>
{
    private List<User> mUserList;
    private MemberClickListener mListener;
    private boolean mShowRemove;

    public SearchMemberAdapter(List<User> mUserList, MemberClickListener mListener, boolean mShowRemove)
    {
        this.mUserList = mUserList;
        this.mListener = mListener;
        this.mShowRemove = mShowRemove;
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
        ListViewMembersBinding mBinding = DataBindingUtil.inflate(
                LayoutInflater.from(parent.getContext()), R.layout.list_view_members,
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

        //show member list
        if (!TextUtils.isEmpty(item.getUserName()))
            holder.mBinding.name.setText(item.getUserName());
        holder.mBinding.email.setText(item.getEmail());
        holder.mBinding.mainView.setOnClickListener(view -> {
            if (mListener != null)
                mListener.onMemberClick(item);
        });

        if (mShowRemove)
        {
            holder.mBinding.remove.setVisibility(View.VISIBLE);
            holder.mBinding.remove.setOnClickListener(view -> {
                if (mListener != null)
                    mListener.onRemove(item);
            });
        }
        else
        {
            holder.mBinding.remove.setVisibility(View.GONE);

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
        ListViewMembersBinding mBinding;

        //**********************************************
        public ViewHolder(@NonNull ListViewMembersBinding itemView)
        //**********************************************
        {
            super(itemView.getRoot());
            mBinding = itemView;
        }
    }


    public interface MemberClickListener
    {
        void onMemberClick(User user);

        void onRemove(User user);
    }
}
