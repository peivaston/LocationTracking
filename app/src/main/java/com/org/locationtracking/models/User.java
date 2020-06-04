package com.org.locationtracking.models;

import android.os.Parcel;
import android.os.Parcelable;

import java.util.List;

import lombok.Getter;
import lombok.Setter;

@Setter
@Getter
public class User
        implements Parcelable
{
    //user detail model
    private String userName;
    private String email;
    private String id;
    private String password;
    private boolean child;
    private String parentId;
    private String fcmToken;
    private double latitude;
    private double longitude;
    private List<String> sharedBy;

    public boolean isChild()
    {
        return child;
    }

    public void setChild(boolean child)
    {
        this.child = child;
    }

    public User()
    {
    }

    public User(String userName, String email, String id, String password)
    {
        this.userName = userName;
        this.email = email;
        this.id = id;
        this.password = password;
    }

    protected User(Parcel in)
    {
        userName = in.readString();
        email = in.readString();
        id = in.readString();
        password = in.readString();
        parentId = in.readString();
    }

    public static final Creator<User> CREATOR = new Creator<User>()
    {
        @Override
        public User createFromParcel(Parcel in)
        {
            return new User(in);
        }

        @Override
        public User[] newArray(int size)
        {
            return new User[size];
        }
    };

    @Override
    public int describeContents()
    {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags)
    {
        dest.writeString(userName);
        dest.writeString(email);
        dest.writeString(id);
        dest.writeString(password);
        dest.writeString(parentId);
    }
}
